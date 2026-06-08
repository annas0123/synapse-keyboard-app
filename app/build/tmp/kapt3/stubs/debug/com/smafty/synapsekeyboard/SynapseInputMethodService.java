package com.smafty.synapsekeyboard;

/**
 * SynapseInputMethodService — the real IME.
 *
 * Hosts a Jetpack Compose view tree inside an InputMethodService by
 * implementing the minimal LifecycleOwner and SavedStateRegistryOwner
 * plumbing that ComposeView requires.
 *
 * Lifecycle architecture (per context/21_ime_lifecycle_management.md):
 * - [serviceScope]  — persistent, tied to the Service lifetime. Used for Room
 *                     Flow collectors and background DB writes that must survive
 *                     even when the keyboard window is hidden.
 * - [imeScope]      — dynamic, tied to the visible keyboard window. Created fresh
 *                     in onStartInputView and cancelled in onFinishInputView.
 *                     NEVER use GlobalScope or MainScope() inside the IME.
 *
 * TextEditorCore integration (per context/22_text_editor_core_architecture.md):
 * - [textEditorCore] is updated with the live InputConnection on every
 *   onStartInputView, and nulled out on every onFinishInputView.
 * - clearHistory() is called on every new input field to sandbox Undo/Redo.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00ca\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0011\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u00012\u00020\u00022\u00020\u00032\u00020\u0004B\u0005\u00a2\u0006\u0002\u0010\u0005J\b\u00102\u001a\u000203H\u0002J>\u00104\u001a\u001a\u0012\u0004\u0012\u00020\u0013\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u000206\u0012\u0004\u0012\u00020605052\u0006\u00107\u001a\u00020\u00132\u0006\u00108\u001a\u00020\u00132\u0006\u00109\u001a\u00020\u0013H\u0082@\u00a2\u0006\u0002\u0010:J\b\u0010;\u001a\u000203H\u0002J\u0010\u0010<\u001a\u00020\u00132\u0006\u0010=\u001a\u00020\u0013H\u0002J\b\u0010>\u001a\u000203H\u0002J\u0010\u0010?\u001a\u0002032\u0006\u0010@\u001a\u00020\u0013H\u0002J\b\u0010A\u001a\u000203H\u0002J\b\u0010B\u001a\u000203H\u0002J\b\u0010C\u001a\u000203H\u0002J\u0010\u0010D\u001a\u0002032\u0006\u0010E\u001a\u000206H\u0002J\b\u0010F\u001a\u00020\u0013H\u0002J\u0016\u0010G\u001a\b\u0012\u0004\u0012\u00020\u00130H2\u0006\u0010I\u001a\u00020JH\u0002J\u0018\u0010K\u001a\u0002032\u0006\u0010L\u001a\u0002062\u0006\u0010M\u001a\u00020NH\u0002J\b\u0010O\u001a\u000203H\u0016J\b\u0010P\u001a\u00020QH\u0016J\b\u0010R\u001a\u000203H\u0016J\u0010\u0010S\u001a\u0002032\u0006\u0010T\u001a\u00020NH\u0016J\u001a\u0010U\u001a\u0002032\b\u0010V\u001a\u0004\u0018\u00010W2\u0006\u0010X\u001a\u00020NH\u0016J\b\u0010Y\u001a\u000203H\u0002J\u0010\u0010Z\u001a\u0002032\u0006\u00107\u001a\u00020\u0013H\u0002J\b\u0010[\u001a\u000203H\u0002J6\u0010\\\u001a\u001a\u0012\u0004\u0012\u00020\u0013\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u000206\u0012\u0004\u0012\u00020605052\u0006\u00107\u001a\u00020\u00132\u0006\u00108\u001a\u00020\u0013H\u0082@\u00a2\u0006\u0002\u0010]J\u000e\u0010^\u001a\u0002032\u0006\u0010_\u001a\u00020`J\u000e\u0010a\u001a\u0002032\u0006\u0010b\u001a\u00020cJ\u0014\u0010d\u001a\u0002032\f\u0010e\u001a\b\u0012\u0004\u0012\u00020\u00130HJ\u0010\u0010f\u001a\u0002032\u0006\u0010@\u001a\u00020\u0013H\u0002J\u001a\u0010g\u001a\u0002032\u0006\u0010h\u001a\u00020\u00132\b\b\u0002\u0010i\u001a\u00020\u0013H\u0002J\u0010\u0010j\u001a\u0002032\u0006\u0010k\u001a\u00020\u0013H\u0002R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082.\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0016\u001a\u00020\u00178BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u0018\u0010\u0019R\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010 \u001a\u00020!8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\"\u0010#R\u000e\u0010$\u001a\u00020%X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010&\u001a\u00020\'8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b(\u0010)R\u000e\u0010*\u001a\u00020+X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020.X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010/\u001a\u00020\u000b8VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b0\u00101\u00a8\u0006l"}, d2 = {"Lcom/smafty/synapsekeyboard/SynapseInputMethodService;", "Landroid/inputmethodservice/InputMethodService;", "Landroidx/lifecycle/LifecycleOwner;", "Landroidx/savedstate/SavedStateRegistryOwner;", "Landroidx/lifecycle/ViewModelStoreOwner;", "()V", "_lifecycleRegistry", "Landroidx/lifecycle/LifecycleRegistry;", "_savedStateRegistryController", "Landroidx/savedstate/SavedStateRegistryController;", "_viewModelStore", "Landroidx/lifecycle/ViewModelStore;", "aiEngine", "Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine;", "clipboardListener", "Landroid/content/ClipboardManager$OnPrimaryClipChangedListener;", "clipboardRepository", "Lcom/smafty/synapsekeyboard/data/local/repository/ClipboardRepository;", "currentUserId", "", "db", "Lcom/smafty/synapsekeyboard/data/local/SynapseDatabase;", "httpClient", "Lokhttp3/OkHttpClient;", "getHttpClient", "()Lokhttp3/OkHttpClient;", "httpClient$delegate", "Lkotlin/Lazy;", "imeScope", "Lkotlinx/coroutines/CoroutineScope;", "kbState", "Lcom/smafty/synapsekeyboard/ui/keyboard/KeyboardUiState;", "lifecycle", "Landroidx/lifecycle/Lifecycle;", "getLifecycle", "()Landroidx/lifecycle/Lifecycle;", "prefListener", "Landroid/content/SharedPreferences$OnSharedPreferenceChangeListener;", "savedStateRegistry", "Landroidx/savedstate/SavedStateRegistry;", "getSavedStateRegistry", "()Landroidx/savedstate/SavedStateRegistry;", "serviceRecomposer", "Landroidx/compose/runtime/Recomposer;", "serviceScope", "textEditorCore", "Lcom/smafty/synapsekeyboard/editor/TextEditorCore;", "viewModelStore", "getViewModelStore", "()Landroidx/lifecycle/ViewModelStore;", "acceptAiSuggestion", "", "callOpenRouter", "Lkotlin/Pair;", "", "text", "prompt", "model", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "cancelAiProcessing", "cleanModelResponse", "content", "clearClipboardHistory", "deleteClipboardItem", "id", "doBackspace", "doReturn", "doSpace", "executeDpadAction", "actionId", "extractTargetText", "loadVisibleTools", "", "prefs", "Landroid/content/SharedPreferences;", "moveCursor", "direction", "selectMode", "", "onCreate", "onCreateInputView", "Landroid/view/View;", "onDestroy", "onFinishInputView", "finishingInput", "onStartInputView", "info", "Landroid/view/inputmethod/EditorInfo;", "restarting", "openKeyboardSettings", "pasteClipboardItem", "rejectAiSuggestion", "runAiPrompt", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveKeyScale", "scale", "", "saveTheme", "theme", "Lcom/smafty/synapsekeyboard/ui/keyboard/KeyboardTheme;", "saveVisibleTools", "tools", "togglePinClipboardItem", "triggerAi", "action", "title", "typeChar", "char", "app_debug"})
public final class SynapseInputMethodService extends android.inputmethodservice.InputMethodService implements androidx.lifecycle.LifecycleOwner, androidx.savedstate.SavedStateRegistryOwner, androidx.lifecycle.ViewModelStoreOwner {
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LifecycleRegistry _lifecycleRegistry = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.savedstate.SavedStateRegistryController _savedStateRegistryController = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.ViewModelStore _viewModelStore = null;
    
    /**
     * Persistent scope for the entire Service lifetime.
     * Use for Room DB observers and clipboard listeners that should survive
     * keyboard hide/show cycles.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.Recomposer serviceRecomposer = null;
    
    /**
     * Dynamic scope tied strictly to the visible keyboard window.
     * Created fresh in onStartInputView; cancelled and nulled in onFinishInputView.
     * Rule: Always use launchIn(imeScope ?: return) — never raw .collect {}.
     */
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.CoroutineScope imeScope;
    @org.jetbrains.annotations.NotNull()
    private final com.smafty.synapsekeyboard.editor.TextEditorCore textEditorCore = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy httpClient$delegate = null;
    private com.smafty.synapsekeyboard.engine.OnDemandAiExecutionEngine aiEngine;
    @org.jetbrains.annotations.NotNull()
    private final com.smafty.synapsekeyboard.ui.keyboard.KeyboardUiState kbState = null;
    private com.smafty.synapsekeyboard.data.local.SynapseDatabase db;
    private com.smafty.synapsekeyboard.data.local.repository.ClipboardRepository clipboardRepository;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String currentUserId = "";
    @org.jetbrains.annotations.Nullable()
    private android.content.ClipboardManager.OnPrimaryClipChangedListener clipboardListener;
    @org.jetbrains.annotations.NotNull()
    private final android.content.SharedPreferences.OnSharedPreferenceChangeListener prefListener = null;
    
    public SynapseInputMethodService() {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public androidx.lifecycle.Lifecycle getLifecycle() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public androidx.savedstate.SavedStateRegistry getSavedStateRegistry() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public androidx.lifecycle.ViewModelStore getViewModelStore() {
        return null;
    }
    
    private final okhttp3.OkHttpClient getHttpClient() {
        return null;
    }
    
    /**
     * Reads the saved tool list from SharedPreferences. Falls back to all three tools.
     */
    private final java.util.List<java.lang.String> loadVisibleTools(android.content.SharedPreferences prefs) {
        return null;
    }
    
    /**
     * Persists the selected tool list to SharedPreferences.
     */
    public final void saveVisibleTools(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> tools) {
    }
    
    /**
     * Persists the selected keyboard theme to SharedPreferences and applies it live.
     */
    public final void saveTheme(@org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.ui.keyboard.KeyboardTheme theme) {
    }
    
    /**
     * Persists the keyboard height scale to SharedPreferences and applies it live.
     */
    public final void saveKeyScale(float scale) {
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    /**
     * Called every time the keyboard window becomes visible (new field focus or restart).
     *
     * IME lifecycle rules applied here:
     * 1. Cancel any stale imeScope (defensive — handles edge cases where
     *    onFinishInputView was skipped by the OS).
     * 2. Allocate a fresh CoroutineScope for this typing session.
     * 3. Hand the live InputConnection to TextEditorCore.
     * 4. Clear Undo/Redo history to sandbox this field's state.
     */
    @java.lang.Override()
    public void onStartInputView(@org.jetbrains.annotations.Nullable()
    android.view.inputmethod.EditorInfo info, boolean restarting) {
    }
    
    /**
     * Called when the keyboard window is hidden or focus moves to a non-text field.
     *
     * IME lifecycle rules applied here:
     * 1. Cancel all coroutines in the typing session scope (UI updates, API tasks).
     * 2. Null the reference so GC can reclaim the scope immediately.
     * 3. Disconnect TextEditorCore to prevent stale InputConnection usage.
     */
    @java.lang.Override()
    public void onFinishInputView(boolean finishingInput) {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.view.View onCreateInputView() {
        return null;
    }
    
    private final void typeChar(java.lang.String p0_1526187) {
    }
    
    private final void doBackspace() {
    }
    
    private final void doReturn() {
    }
    
    private final void doSpace() {
    }
    
    /**
     * Extracts the current target text for AI processing.
     * Uses TextEditorCore's abbreviation-aware sentence isolator.
     */
    private final java.lang.String extractTargetText() {
        return null;
    }
    
    /**
     * Triggers an AI prompt run.
     * @param action  The full prompt instruction string sent to the AI.
     * @param title   Human-readable display name used by the Most Active tracker.
     *               Defaults to the first 30 chars of [action] if not supplied.
     */
    private final void triggerAi(java.lang.String action, java.lang.String title) {
    }
    
    /**
     * Suspend function invoked by [aiEngine] on Dispatchers.IO.
     *
     * Returns a Pair of:
     * - The cleaned AI result text
     * - A nested Pair of (inputWords, outputWords) derived from API token usage × 0.75
     */
    private final java.lang.Object runAiPrompt(java.lang.String text, java.lang.String prompt, kotlin.coroutines.Continuation<? super kotlin.Pair<java.lang.String, kotlin.Pair<java.lang.Integer, java.lang.Integer>>> $completion) {
        return null;
    }
    
    /**
     * Executes an HTTP request against OpenRouter with the given model.
     *
     * Returns Pair<cleanedText, Pair<inputWords, outputWords>> where word counts
     * are derived from the API's usage object: tokens × 0.75 (rounded to nearest int).
     */
    private final java.lang.Object callOpenRouter(java.lang.String text, java.lang.String prompt, java.lang.String model, kotlin.coroutines.Continuation<? super kotlin.Pair<java.lang.String, kotlin.Pair<java.lang.Integer, java.lang.Integer>>> $completion) {
        return null;
    }
    
    /**
     * Cleans up the model response by removing any markdown wrappers or surrounding quotes
     * that the model may have output despite the system guidelines.
     */
    private final java.lang.String cleanModelResponse(java.lang.String content) {
        return null;
    }
    
    /**
     * Cancels any active AI job — tied to the Cancel button and IME close.
     */
    private final void cancelAiProcessing() {
    }
    
    private final void acceptAiSuggestion() {
    }
    
    private final void rejectAiSuggestion() {
    }
    
    /**
     * Pastes a clipboard item into the active field using TextEditorCore's batch edit.
     * This guarantees the paste is atomic and cursor position is correctly maintained.
     * Verifies InputConnection is active before attempting paste (doc 19 safe paste rule).
     */
    private final void pasteClipboardItem(java.lang.String text) {
    }
    
    private final void openKeyboardSettings() {
    }
    
    /**
     * Moves the cursor in the given direction.
     * If [selectMode] is true, the cursor movement extends a text selection
     * (simulates holding Shift). TextEditorCore handles the native KeyEvent
     * dispatch, including line-wrap math which the IME cannot compute directly.
     *
     * @param direction 1=Up, 2=Down, 3=Left, 4=Right
     * @param selectMode true to extend selection instead of moving cursor
     */
    private final void moveCursor(int direction, boolean selectMode) {
    }
    
    /**
     * Executes a D-Pad panel context action (Select All, Copy, Cut, Paste, Undo, Redo).
     * Uses performContextMenuAction for maximum app compatibility.
     *
     * @param actionId 1=SelectAll, 2=Copy, 3=Cut, 4=Paste, 5=Undo, 6=Redo
     */
    private final void executeDpadAction(int actionId) {
    }
    
    private final void togglePinClipboardItem(java.lang.String id) {
    }
    
    private final void deleteClipboardItem(java.lang.String id) {
    }
    
    private final void clearClipboardHistory() {
    }
}