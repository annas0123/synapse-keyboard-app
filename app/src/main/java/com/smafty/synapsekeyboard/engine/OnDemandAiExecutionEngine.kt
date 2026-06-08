package com.smafty.synapsekeyboard.engine

import android.view.KeyEvent
import com.smafty.synapsekeyboard.data.local.dao.RecentAiOutputDao
import com.smafty.synapsekeyboard.editor.TextEditorCore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * OnDemandAiExecutionEngine — high-performance, non-blocking AI execution engine.
 *
 * Architecture (per context/23_input_debounce_engine.md):
 * - AI processing is ONLY triggered on-demand when the user taps a prompt pill.
 *   It is NEVER triggered automatically during typing.
 * - While in PROCESSING state, all keystrokes are intercepted into a local
 *   [EngineState.Processing.tempBuffer] instead of being sent to the host app's
 *   InputConnection (prevents cursor corruption in the host app).
 * - On AI completion, the engine atomically merges the AI output with the
 *   temp buffer and replaces the originally targeted text via [TextEditorCore].
 * - A Cancel button or keyboard close instantly aborts the background job.
 *
 * @param serviceScope  Persistent Service-level scope. Used for the AI background coroutine
 *                      so it survives brief keyboard hide/show events.
 * @param textEditorCore The active TextEditorCore providing atomic text replacement.
 * @param recentAiOutputDao DAO to persist the last 10 AI outputs for the history panel.
 * @param onEnergyConsumed Callback invoked with (inputEnergy, outputEnergy) immediately after a
 *                         successful API call so the service can deduct quota from Supabase.
 * @param executeAiPromptApi Suspend lambda: (originalText, promptTemplate) –>
 *                           Pair<resultText, Pair<inputEnergy, outputEnergy>>.
 */
class OnDemandAiExecutionEngine(
    private val serviceScope: CoroutineScope,
    private val textEditorCore: TextEditorCore,
    private val recentAiOutputDao: RecentAiOutputDao,
    private val onEnergyConsumed: (inputEnergy: Int, outputEnergy: Int) -> Unit,
    private val executeAiPromptApi: suspend (String, String) -> Pair<String, Pair<Int, Int>>
) {

    // -----------------------------------------------------------------------
    // State machine
    // -----------------------------------------------------------------------

    /**
     * Sealed class representing the engine's current state.
     * Observed by the keyboard UI to render the appropriate toolbar view.
     */
    sealed class EngineState {
        /** Normal operation — keystrokes flow directly to the host app. */
        object Idle : EngineState()

        /**
         * AI is processing. All new keystrokes are buffered locally in [tempBuffer]
         * instead of being committed to the host app's InputConnection.
         */
        data class Processing(val tempBuffer: String) : EngineState()

        /**
         * A recoverable error occurred (timeout, network failure, etc.).
         * Automatically resets to [Idle] after 2 seconds via [resetToIdleDelayed].
         */
        data class Error(val message: String) : EngineState()
    }

    private val _engineState = MutableStateFlow<EngineState>(EngineState.Idle)

    /** Observed by the Compose UI to render the loading/cancel/error toolbar overlay. */
    val engineState: StateFlow<EngineState> = _engineState.asStateFlow()

    private var activeAiJob: Job? = null

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Triggered when the user explicitly taps a prompt pill/template.
     * Starts the AI background task on [serviceScope] — which persists even if the
     * keyboard window is briefly hidden mid-processing.
     *
     * @param originalText   The text isolated by TextEditorCore (selected or sentence boundary).
     * @param promptTemplate The instruction string (e.g. "Fix Grammar", "Translate to Urdu").
     */
    fun executePrompt(originalText: String, promptTemplate: String) {
        cancelActiveJob() // Safe clean-up of any previous running job

        activeAiJob = serviceScope.launch(Dispatchers.Main) {
            try {
                // Step 1: Transition to Processing state — empty local buffer
                _engineState.value = EngineState.Processing(tempBuffer = "")

                // Step 2: Fetch AI result on IO (strict 10-second timeout)
                val (aiResult, energyCounts) = withContext(Dispatchers.IO) {
                    withTimeout(10_000L) {
                        executeAiPromptApi(originalText, promptTemplate)
                    }
                }
                val (inputEnergy, outputEnergy) = energyCounts

                // Step 3: Report AI energy consumption immediately (Deducted on generation, not on accept)
                onEnergyConsumed(inputEnergy, outputEnergy)

                // Step 4: Persist result to recent outputs history (auto-pruned to 10)
                withContext(Dispatchers.IO) {
                    recentAiOutputDao.safeInsertAndPrune(aiResult)
                }

                // Step 5: Atomic Merge & Commit — only if keyboard is still active
                val currentState = _engineState.value
                if (currentState is EngineState.Processing) {
                    val mergedText = aiResult + currentState.tempBuffer
                    textEditorCore.replaceTextAtomically(mergedText)
                }

                _engineState.value = EngineState.Idle

            } catch (e: TimeoutCancellationException) {
                _engineState.value = EngineState.Error("Connection timed out. Please try again.")
                resetToIdleDelayed()
            } catch (e: Exception) {
                // If the job was cancelled (user pressed Cancel), don't show an error
                if (activeAiJob?.isCancelled == true) {
                    _engineState.value = EngineState.Idle
                } else {
                    _engineState.value = EngineState.Error(e.message ?: "Failed to process text.")
                    resetToIdleDelayed()
                }
            }
        }
    }

    /**
     * Intercepts a keystroke while the engine is in [EngineState.Processing].
     *
     * Returns TRUE if the key was consumed (intercepted into the local buffer).
     * Returns FALSE if the key should be committed normally to the host app.
     *
     * Called by the IME service's key dispatch before any InputConnection send.
     */
    fun interceptKeystroke(keyCode: Int, unicodeChar: Char): Boolean {
        val currentState = _engineState.value
        if (currentState !is EngineState.Processing) {
            return false // Idle: allow normal host-app commit
        }

        when (keyCode) {
            KeyEvent.KEYCODE_DEL -> {
                // Backspace: remove last char from local buffer
                if (currentState.tempBuffer.isNotEmpty()) {
                    _engineState.value = EngineState.Processing(currentState.tempBuffer.dropLast(1))
                }
            }
            KeyEvent.KEYCODE_SPACE -> {
                _engineState.value = EngineState.Processing(currentState.tempBuffer + " ")
            }
            // Consume arrow keys — prevents cursor drift in the host app during AI processing
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_RIGHT,
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                // Silently consumed
            }
            else -> {
                // Printable characters only — ignore control chars
                if (unicodeChar != '\u0000' && !Character.isISOControl(unicodeChar)) {
                    _engineState.value = EngineState.Processing(currentState.tempBuffer + unicodeChar)
                }
            }
        }
        return true // Key consumed locally — do NOT send to host app
    }

    /**
     * Immediately aborts the active AI request, clears the temp buffer, and
     * restores [EngineState.Idle]. Tied to the Cancel button and [onFinishInputView].
     */
    fun cancelActiveJob() {
        activeAiJob?.let { job ->
            if (job.isActive) job.cancel()
        }
        activeAiJob = null
        _engineState.value = EngineState.Idle
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /** Shows the error state for 2 seconds, then resets to Idle. */
    private fun resetToIdleDelayed() {
        serviceScope.launch {
            delay(2_000L)
            _engineState.value = EngineState.Idle
        }
    }
}
