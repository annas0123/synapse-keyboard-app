package com.smafty.synapsekeyboard.editor

import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import java.util.Stack

/**
 * TextEditorCore — encapsulates all complex interactions with Android's volatile
 * InputConnection API, keeping SynapseInputMethodService clean, stable, and modular.
 *
 * Key architecture decisions (per context/22_text_editor_core_architecture.md):
 *  - D-Pad events are simulated as native physical KeyEvents to let the OS handle
 *    line-wrap math — solving the multi-line DPAD wrap block.
 *  - Undo/Redo history is sandboxed (cleared on every new input field) to prevent
 *    cross-app state leaks and password-field data bleeds.
 *  - Text boundary extraction uses an abbreviation-aware sentence isolator instead
 *    of naive indexOf('.') splits.
 */
class TextEditorCore {

    private var activeConnection: InputConnection? = null

    /** True while the user has toggled "Select mode" (simulates holding Shift). */
    var isSelectionModeActive: Boolean = false
        private set

    // Sandboxed stacks — cleared every time a new input field gains focus.
    private val undoStack = Stack<EditorState>()
    private val redoStack = Stack<EditorState>()

    /** Snapshot of editor content used for Undo / Redo operations. */
    data class EditorState(
        val text: String,
        val selectionStart: Int,
        val selectionEnd: Int
    )

    // ==========================================
    // Connection management
    // ==========================================

    /** Call from onStartInputView / onFinishInputView to keep the connection fresh. */
    fun updateInputConnection(connection: InputConnection?) {
        activeConnection = connection
    }

    /**
     * Resets the Undo/Redo history and selection mode.
     * MUST be called in onStartInputView to prevent cross-app text bleeding.
     */
    fun clearHistory() {
        undoStack.clear()
        redoStack.clear()
        isSelectionModeActive = false
    }

    // ==========================================
    // A. Cursor & Selection Movement Engine
    // ==========================================

    /**
     * Toggles the simulated "Shift" key state.
     * When true, moving the cursor expands the text selection.
     */
    fun toggleSelectionMode(active: Boolean) {
        isSelectionModeActive = active
    }

    /**
     * Resets selection mode safely.
     * Call this when switching layouts (e.g., leaving D-Pad panel back to QWERTY)
     * to avoid cursor drag glitches during regular typing.
     */
    fun resetSelectionMode() {
        isSelectionModeActive = false
    }

    /** Moves the cursor left; if selection mode is active, extends the selection. */
    fun moveCursorLeft()  = sendDpadEvent(KeyEvent.KEYCODE_DPAD_LEFT)

    /** Moves the cursor right; if selection mode is active, extends the selection. */
    fun moveCursorRight() = sendDpadEvent(KeyEvent.KEYCODE_DPAD_RIGHT)

    /**
     * Moves the cursor up one visual line.
     * Uses a native KeyEvent so the OS handles line-wrap boundaries automatically —
     * an IME cannot see the host app's font size or text view width.
     */
    fun moveCursorUp()    = sendDpadEvent(KeyEvent.KEYCODE_DPAD_UP)

    /** Moves the cursor down one visual line (same approach as moveCursorUp). */
    fun moveCursorDown()  = sendDpadEvent(KeyEvent.KEYCODE_DPAD_DOWN)

    /**
     * Core D-Pad event simulation.
     * Dispatches physical KeyEvents on the active InputConnection. If selection
     * mode is active, META_SHIFT_ON is applied to create a text selection.
     */
    private fun sendDpadEvent(keyCode: Int): Boolean {
        val connection = activeConnection ?: return false
        val downTime = android.os.SystemClock.uptimeMillis()
        return if (isSelectionModeActive) {
            connection.sendKeyEvent(
                KeyEvent(downTime, downTime, KeyEvent.ACTION_DOWN, keyCode, 0, KeyEvent.META_SHIFT_ON)
            )
            connection.sendKeyEvent(
                KeyEvent(downTime, downTime, KeyEvent.ACTION_UP, keyCode, 0, KeyEvent.META_SHIFT_ON)
            )
        } else {
            connection.sendKeyEvent(
                KeyEvent(downTime, downTime, KeyEvent.ACTION_DOWN, keyCode, 0, 0)
            )
            connection.sendKeyEvent(
                KeyEvent(downTime, downTime, KeyEvent.ACTION_UP, keyCode, 0, 0)
            )
        }
    }

    /** Selects all text in the currently focused input field. */
    fun selectAll(): Boolean {
        val connection = activeConnection ?: return false
        return connection.performContextMenuAction(android.R.id.selectAll)
    }

    /** Returns the currently highlighted text block, or null if nothing is selected. */
    fun getSelectedText(): String? {
        val connection = activeConnection ?: return null
        return connection.getSelectedText(0)?.toString()
    }

    // ==========================================
    // B. Smart Text Extraction & Replacements
    // ==========================================

    /**
     * Extracts either the active selection, or falls back to the current sentence
     * using abbreviation-aware boundary detection.
     */
    fun extractTargetText(): String {
        activeConnection ?: return ""
        val selection = getSelectedText()
        if (!selection.isNullOrEmpty()) return selection

        val connection = activeConnection ?: return ""
        val before = connection.getTextBeforeCursor(300, 0)?.toString() ?: ""
        val after  = connection.getTextAfterCursor(300, 0)?.toString()  ?: ""

        return isolateCurrentSentence(before, after)
    }

    /**
     * Advanced Sentence Boundary Isolation.
     * Intelligently parses boundaries by skipping common abbreviations and email dots,
     * and only splits when sentence-ending punctuation is followed by whitespace.
     */
    fun isolateCurrentSentence(before: String, after: String): String {
        if (before.isEmpty() && after.isEmpty()) return ""

        val abbreviations = listOf("Dr.", "Mr.", "Mrs.", "Ms.", "e.g.", "i.e.", "vs.")

        // 1. Locate start of the current sentence inside the "before" block.
        var startIndex = 0
        for (i in before.length - 2 downTo 0) {
            val char = before[i]
            if (char == '.' || char == '!' || char == '?' || char == '\n') {
                val isAbbrev = abbreviations.any { abbrev ->
                    before.substring(0, i + 1).endsWith(abbrev, ignoreCase = true)
                }
                if (!isAbbrev) {
                    if (i + 1 < before.length && before[i + 1].isWhitespace()) {
                        startIndex = i + 1
                        break
                    }
                }
            }
        }
        val lastSentencePart = before.substring(startIndex)

        // 2. Locate end of the current sentence inside the "after" block.
        var endIndex = after.length
        for (i in 0 until after.length) {
            val char = after[i]
            if (char == '.' || char == '!' || char == '?' || char == '\n') {
                val isAbbrev = abbreviations.any { abbrev ->
                    (lastSentencePart + after.substring(0, i + 1)).endsWith(abbrev, ignoreCase = true)
                }
                if (!isAbbrev) {
                    endIndex = i + 1
                    break
                }
            }
        }
        val firstSentencePart = after.substring(0, endIndex)

        return (lastSentencePart + firstSentencePart).trim()
    }

    /**
     * Executes an atomic batch text replacement.
     * Selects the entire content of the input field and replaces it with [newText].
     * Wrapped in beginBatchEdit / endBatchEdit to appear as a single undo unit.
     */
    fun replaceTextAtomically(newText: String): Boolean {
        val connection = activeConnection ?: return false
        connection.beginBatchEdit()
        selectAll()
        connection.commitText(newText, 1)
        connection.endBatchEdit()
        return true
    }

    // ==========================================
    // C. Sandbox Undo / Redo Operations
    // ==========================================

    /**
     * Saves a snapshot of the editor state before a destructive operation.
     * Enforces a max depth of 20 states to prevent RAM bloat.
     */
    fun saveBeforeState(currentState: EditorState) {
        if (undoStack.size > 20) undoStack.removeAt(0)
        undoStack.push(currentState)
        redoStack.clear() // Any new action invalidates the redo history.
    }

    /**
     * Reverts the editor to the most recently saved state.
     * Returns the restored [EditorState], or null if the stack is empty.
     */
    fun performUndo(): EditorState? {
        val connection = activeConnection ?: return null
        if (undoStack.isEmpty()) return null

        val previousState = undoStack.pop()
        connection.beginBatchEdit()
        selectAll()
        connection.commitText(previousState.text, 1)
        connection.setSelection(previousState.selectionStart, previousState.selectionEnd)
        connection.endBatchEdit()

        return previousState
    }
}
