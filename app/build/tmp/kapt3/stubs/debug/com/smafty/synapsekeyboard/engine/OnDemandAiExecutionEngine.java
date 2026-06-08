package com.smafty.synapsekeyboard.engine;

/**
 * OnDemandAiExecutionEngine — high-performance, non-blocking AI execution engine.
 *
 * Architecture (per context/23_input_debounce_engine.md):
 * - AI processing is ONLY triggered on-demand when the user taps a prompt pill.
 *  It is NEVER triggered automatically during typing.
 * - While in PROCESSING state, all keystrokes are intercepted into a local
 *  [EngineState.Processing.tempBuffer] instead of being sent to the host app's
 *  InputConnection (prevents cursor corruption in the host app).
 * - On AI completion, the engine atomically merges the AI output with the
 *  temp buffer and replaces the originally targeted text via [TextEditorCore].
 * - A Cancel button or keyboard close instantly aborts the background job.
 *
 * @param serviceScope  Persistent Service-level scope. Used for the AI background coroutine
 *                     so it survives brief keyboard hide/show events.
 * @param textEditorCore The active TextEditorCore providing atomic text replacement.
 * @param recentAiOutputDao DAO to persist the last 10 AI outputs for the history panel.
 * @param onEnergyConsumed Callback invoked with (inputEnergy, outputEnergy) immediately after a
 *                        successful API call so the service can deduct quota from Supabase.
 * @param executeAiPromptApi Suspend lambda: (originalText, promptTemplate) –>
 *                          Pair<resultText, Pair<inputEnergy, outputEnergy>>.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\f\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001:\u0001*B\u0097\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00126\u0010\b\u001a2\u0012\u0013\u0012\u00110\n\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u0012\u0013\u0012\u00110\n\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u000f0\t\u0012@\u0010\u0010\u001a<\b\u0001\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u0012\u0012\"\u0012 \u0012\u001c\u0012\u001a\u0012\u0004\u0012\u00020\u0012\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n0\u00140\u00140\u0013\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0011\u00a2\u0006\u0002\u0010\u0015J\u0006\u0010 \u001a\u00020\u000fJ\u0016\u0010!\u001a\u00020\u000f2\u0006\u0010\"\u001a\u00020\u00122\u0006\u0010#\u001a\u00020\u0012J\u0016\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\n2\u0006\u0010\'\u001a\u00020(J\b\u0010)\u001a\u00020\u000fH\u0002R\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00180\u001c\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eRJ\u0010\u0010\u001a<\b\u0001\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u0012\u0012\"\u0012 \u0012\u001c\u0012\u001a\u0012\u0004\u0012\u00020\u0012\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n0\u00140\u00140\u0013\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0011X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u001fR>\u0010\b\u001a2\u0012\u0013\u0012\u00110\n\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u0012\u0013\u0012\u00110\n\u00a2\u0006\f\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u000f0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006+"}, d2 = {"Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine;", "", "serviceScope", "Lkotlinx/coroutines/CoroutineScope;", "textEditorCore", "Lcom/smafty/synapsekeyboard/editor/TextEditorCore;", "recentAiOutputDao", "Lcom/smafty/synapsekeyboard/data/local/dao/RecentAiOutputDao;", "onEnergyConsumed", "Lkotlin/Function2;", "", "Lkotlin/ParameterName;", "name", "inputEnergy", "outputEnergy", "", "executeAiPromptApi", "Lkotlin/Function3;", "", "Lkotlin/coroutines/Continuation;", "Lkotlin/Pair;", "(Lkotlinx/coroutines/CoroutineScope;Lcom/smafty/synapsekeyboard/editor/TextEditorCore;Lcom/smafty/synapsekeyboard/data/local/dao/RecentAiOutputDao;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function3;)V", "_engineState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine$EngineState;", "activeAiJob", "Lkotlinx/coroutines/Job;", "engineState", "Lkotlinx/coroutines/flow/StateFlow;", "getEngineState", "()Lkotlinx/coroutines/flow/StateFlow;", "Lkotlin/jvm/functions/Function3;", "cancelActiveJob", "executePrompt", "originalText", "promptTemplate", "interceptKeystroke", "", "keyCode", "unicodeChar", "", "resetToIdleDelayed", "EngineState", "app_debug"})
public final class OnDemandAiExecutionEngine {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    @org.jetbrains.annotations.NotNull()
    private final com.smafty.synapsekeyboard.editor.TextEditorCore textEditorCore = null;
    @org.jetbrains.annotations.NotNull()
    private final com.smafty.synapsekeyboard.data.local.dao.RecentAiOutputDao recentAiOutputDao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function2<java.lang.Integer, java.lang.Integer, kotlin.Unit> onEnergyConsumed = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.jvm.functions.Function3<java.lang.String, java.lang.String, kotlin.coroutines.Continuation<? super kotlin.Pair<java.lang.String, kotlin.Pair<java.lang.Integer, java.lang.Integer>>>, java.lang.Object> executeAiPromptApi = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.smafty.synapsekeyboard.engine.OnDemandAiExecutionEngine.EngineState> _engineState = null;
    
    /**
     * Observed by the Compose UI to render the loading/cancel/error toolbar overlay.
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.smafty.synapsekeyboard.engine.OnDemandAiExecutionEngine.EngineState> engineState = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job activeAiJob;
    
    public OnDemandAiExecutionEngine(@org.jetbrains.annotations.NotNull()
    kotlinx.coroutines.CoroutineScope serviceScope, @org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.editor.TextEditorCore textEditorCore, @org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.data.local.dao.RecentAiOutputDao recentAiOutputDao, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.Integer, ? super java.lang.Integer, kotlin.Unit> onEnergyConsumed, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function3<? super java.lang.String, ? super java.lang.String, ? super kotlin.coroutines.Continuation<? super kotlin.Pair<java.lang.String, kotlin.Pair<java.lang.Integer, java.lang.Integer>>>, ? extends java.lang.Object> executeAiPromptApi) {
        super();
    }
    
    /**
     * Observed by the Compose UI to render the loading/cancel/error toolbar overlay.
     */
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.smafty.synapsekeyboard.engine.OnDemandAiExecutionEngine.EngineState> getEngineState() {
        return null;
    }
    
    /**
     * Triggered when the user explicitly taps a prompt pill/template.
     * Starts the AI background task on [serviceScope] — which persists even if the
     * keyboard window is briefly hidden mid-processing.
     *
     * @param originalText   The text isolated by TextEditorCore (selected or sentence boundary).
     * @param promptTemplate The instruction string (e.g. "Fix Grammar", "Translate to Urdu").
     */
    public final void executePrompt(@org.jetbrains.annotations.NotNull()
    java.lang.String originalText, @org.jetbrains.annotations.NotNull()
    java.lang.String promptTemplate) {
    }
    
    /**
     * Intercepts a keystroke while the engine is in [EngineState.Processing].
     *
     * Returns TRUE if the key was consumed (intercepted into the local buffer).
     * Returns FALSE if the key should be committed normally to the host app.
     *
     * Called by the IME service's key dispatch before any InputConnection send.
     */
    public final boolean interceptKeystroke(int keyCode, char unicodeChar) {
        return false;
    }
    
    /**
     * Immediately aborts the active AI request, clears the temp buffer, and
     * restores [EngineState.Idle]. Tied to the Cancel button and [onFinishInputView].
     */
    public final void cancelActiveJob() {
    }
    
    /**
     * Shows the error state for 2 seconds, then resets to Idle.
     */
    private final void resetToIdleDelayed() {
    }
    
    /**
     * Sealed class representing the engine's current state.
     * Observed by the keyboard UI to render the appropriate toolbar view.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0003\u0003\u0004\u0005B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0003\u0006\u0007\b\u00a8\u0006\t"}, d2 = {"Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine$EngineState;", "", "()V", "Error", "Idle", "Processing", "Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine$EngineState$Error;", "Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine$EngineState$Idle;", "Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine$EngineState$Processing;", "app_debug"})
    public static abstract class EngineState {
        
        private EngineState() {
            super();
        }
        
        /**
         * A recoverable error occurred (timeout, network failure, etc.).
         * Automatically resets to [Idle] after 2 seconds via [resetToIdleDelayed].
         */
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine$EngineState$Error;", "Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine$EngineState;", "message", "", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Error extends com.smafty.synapsekeyboard.engine.OnDemandAiExecutionEngine.EngineState {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String message = null;
            
            public Error(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getMessage() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.smafty.synapsekeyboard.engine.OnDemandAiExecutionEngine.EngineState.Error copy(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
        
        /**
         * Normal operation — keystrokes flow directly to the host app.
         */
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine$EngineState$Idle;", "Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine$EngineState;", "()V", "app_debug"})
        public static final class Idle extends com.smafty.synapsekeyboard.engine.OnDemandAiExecutionEngine.EngineState {
            @org.jetbrains.annotations.NotNull()
            public static final com.smafty.synapsekeyboard.engine.OnDemandAiExecutionEngine.EngineState.Idle INSTANCE = null;
            
            private Idle() {
            }
        }
        
        /**
         * AI is processing. All new keystrokes are buffered locally in [tempBuffer]
         * instead of being committed to the host app's InputConnection.
         */
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u00d6\u0003J\t\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\t\u0010\u000f\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0010"}, d2 = {"Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine$EngineState$Processing;", "Lcom/smafty/synapsekeyboard/engine/OnDemandAiExecutionEngine$EngineState;", "tempBuffer", "", "(Ljava/lang/String;)V", "getTempBuffer", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Processing extends com.smafty.synapsekeyboard.engine.OnDemandAiExecutionEngine.EngineState {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String tempBuffer = null;
            
            public Processing(@org.jetbrains.annotations.NotNull()
            java.lang.String tempBuffer) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getTempBuffer() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.smafty.synapsekeyboard.engine.OnDemandAiExecutionEngine.EngineState.Processing copy(@org.jetbrains.annotations.NotNull()
            java.lang.String tempBuffer) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
    }
}