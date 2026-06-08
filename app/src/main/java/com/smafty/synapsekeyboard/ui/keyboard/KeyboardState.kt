package com.smafty.synapsekeyboard.ui.keyboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// ---------------------------------------------------------------------------
// Keyboard mode (which layer is displayed)
// ---------------------------------------------------------------------------
enum class KeyboardMode { QWERTY, SYMBOLS_1, SYMBOLS_2, EMOJI, CLIPBOARD, TEXT_NAV, AI_PROMPTS, TOOL_MANAGER, THEME_SWITCHER, SIZE_PANEL }

// ---------------------------------------------------------------------------
// Tool identifiers for the customizable center toolbar
// ---------------------------------------------------------------------------
const val TOOL_CLIPBOARD      = "clipboard"
const val TOOL_TEXT_NAV       = "text_nav"
const val TOOL_AI_PROMPTS     = "ai_prompts"
const val TOOL_THEME_SWITCHER = "theme_switcher"
const val TOOL_SIZE_PANEL     = "size_panel"

// ---------------------------------------------------------------------------
// Clipboard Item Model
// ---------------------------------------------------------------------------
data class ClipboardItem(
    val id: String,
    val text: String,
    val isPinned: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

// ---------------------------------------------------------------------------
// AI toolbar state
// ---------------------------------------------------------------------------
enum class AiOutputState { IDLE, LOADING, SHOWING_RESULT }

// ---------------------------------------------------------------------------
// AI Engine processing state (OnDemandAiExecutionEngine integration)
// ---------------------------------------------------------------------------
enum class AiProcessingState { IDLE, PROCESSING, ERROR }

// ---------------------------------------------------------------------------
// KeyboardUiState — single source of truth for keyboard UI, held in the IME
// service and observed by the Compose tree via derivedStateOf / mutableState.
// ---------------------------------------------------------------------------
class KeyboardUiState {
    var isShiftActive by mutableStateOf(false)
    var isCapsLock    by mutableStateOf(false)
    var mode          by mutableStateOf(KeyboardMode.QWERTY)
    var aiOutputState by mutableStateOf(AiOutputState.IDLE)
    var aiSuggestion  by mutableStateOf("")

    // ---------------------------------------------------------------------------
    // Visible tools — list of tool IDs shown in the center of the top toolbar
    // Loaded from SharedPreferences on startup; default = all 3 tools.
    // ---------------------------------------------------------------------------
    var visibleTools by mutableStateOf<List<String>>(listOf(TOOL_CLIPBOARD, TOOL_TEXT_NAV, TOOL_AI_PROMPTS))

    // Engine processing state — mirrors OnDemandAiExecutionEngine.EngineState
    var aiProcessingState  by mutableStateOf(AiProcessingState.IDLE)
    var aiProcessingBuffer by mutableStateOf("")   // Temp buffer shown while AI is running
    var aiErrorMessage     by mutableStateOf("")   // Shown briefly on error, then auto-reset

    // ---------------------------------------------------------------------------
    // AI Energy Quota (Blueprint 26) — local mirror of Supabase energy_quotas
    // ---------------------------------------------------------------------------
    /** Total AI Energy units the user is allowed to consume (default free quota: 20,000). */
    var energyAllowed by mutableStateOf(20_000)
    /** Cumulative AI Energy units consumed — drives the circular gauge in HomeScreen. */
    var energyUsed by mutableStateOf(0)
    /** Energy cost of the LAST prompt's input (for AcceptRejectToolbar badge). */
    var lastInputEnergy by mutableStateOf(0)
    /** Energy cost of the LAST prompt's output (for AcceptRejectToolbar badge). */
    var lastOutputEnergy by mutableStateOf(0)
    /**
     * The instruction string of the currently SELECTED prompt pill (first tap).
     * Empty string means no prompt is selected. A second tap on the same
     * instruction string executes the AI prompt.
     */
    var selectedPromptInstruction by mutableStateOf("")

    val energyRemaining: Int get() = (energyAllowed - energyUsed).coerceAtLeast(0)

    // Keyboard height scale factor — 0.85 (small) to 1.3 (large), default 1.0
    var keyHeightScale by mutableFloatStateOf(1.0f)

    // Active theme preset
    var activeTheme by mutableStateOf(KeyboardTheme.DARK_ELEGANCE)

    // Currently selected emoji category index
    var emojiCategoryIndex by mutableStateOf(0)

    // Clipboard history items
    var clipboardItems by mutableStateOf<List<ClipboardItem>>(emptyList())

    // Stores the mode before switching to emoji or special panels, so we can go back
    private var previousMode: KeyboardMode = KeyboardMode.QWERTY

    fun onShiftTap() {
        when {
            isCapsLock    -> { isCapsLock = false; isShiftActive = false }
            isShiftActive -> { isCapsLock = true }
            else           -> { isShiftActive = true }
        }
    }

    fun consumeShiftAfterKey() {
        if (isShiftActive && !isCapsLock) isShiftActive = false
    }

    fun isUpperCase() = isShiftActive || isCapsLock

    fun switchToEmoji() {
        if (mode != KeyboardMode.EMOJI) {
            previousMode = mode
        }
        mode = KeyboardMode.EMOJI
        emojiCategoryIndex = 0
    }

    fun exitEmoji() {
        mode = previousMode
    }

    fun switchToClipboard() {
        if (mode != KeyboardMode.CLIPBOARD) {
            previousMode = mode
        }
        mode = KeyboardMode.CLIPBOARD
    }

    fun switchToTextNav() {
        if (mode != KeyboardMode.TEXT_NAV) {
            previousMode = mode
        }
        mode = KeyboardMode.TEXT_NAV
    }

    fun switchToAiPrompts() {
        if (mode != KeyboardMode.AI_PROMPTS) {
            previousMode = mode
        }
        mode = KeyboardMode.AI_PROMPTS
    }

    fun switchToToolManager() {
        if (mode != KeyboardMode.TOOL_MANAGER) {
            previousMode = mode
        }
        mode = KeyboardMode.TOOL_MANAGER
    }

    fun switchToThemeSwitcher() {
        if (mode != KeyboardMode.THEME_SWITCHER) {
            previousMode = mode
        }
        mode = KeyboardMode.THEME_SWITCHER
    }

    fun switchToSizePanel() {
        if (mode != KeyboardMode.SIZE_PANEL) {
            previousMode = mode
        }
        mode = KeyboardMode.SIZE_PANEL
    }

    fun exitSpecialMode() {
        mode = previousMode
    }
}
