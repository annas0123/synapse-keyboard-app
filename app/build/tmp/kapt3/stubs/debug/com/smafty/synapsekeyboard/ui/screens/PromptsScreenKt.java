package com.smafty.synapsekeyboard.ui.screens;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000H\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a4\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00060\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\fH\u0003\u001a.\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00060\fH\u0003\u001a&\u0010\u0015\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\u00162\u0006\u0010\t\u001a\u00020\n2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00060\fH\u0003\u001a\u0018\u0010\u0018\u001a\u00020\u00062\u0006\u0010\u0019\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\nH\u0003\u001a:\u0010\u001a\u001a\u00020\u00062\b\u0010\u001b\u001a\u0004\u0018\u00010\b2\u0018\u0010\u001c\u001a\u0014\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00060\u001d2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00060\fH\u0003\u001a\b\u0010\u001f\u001a\u00020\u0006H\u0007\"\u0017\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0003\u0010\u0004\u00a8\u0006 "}, d2 = {"SYNAPSE_PRESETS", "", "Lcom/smafty/synapsekeyboard/ui/screens/PresetPrompt;", "getSYNAPSE_PRESETS", "()Ljava/util/List;", "CustomPromptCard", "", "prompt", "Lcom/smafty/synapsekeyboard/data/local/repository/RemoteCustomPrompt;", "index", "", "onEdit", "Lkotlin/Function0;", "onDelete", "EmptyState", "title", "", "subtitle", "showAdd", "", "onAdd", "MostActiveCard", "Lcom/smafty/synapsekeyboard/data/local/entity/MostUsedPromptEntity;", "onResetUsage", "PresetPromptCard", "preset", "PromptBottomSheet", "editingPrompt", "onSave", "Lkotlin/Function2;", "onDismiss", "PromptsScreen", "app_debug"})
public final class PromptsScreenKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.smafty.synapsekeyboard.ui.screens.PresetPrompt> SYNAPSE_PRESETS = null;
    
    @org.jetbrains.annotations.NotNull()
    public static final java.util.List<com.smafty.synapsekeyboard.ui.screens.PresetPrompt> getSYNAPSE_PRESETS() {
        return null;
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void PromptsScreen() {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CustomPromptCard(com.smafty.synapsekeyboard.data.local.repository.RemoteCustomPrompt prompt, int index, kotlin.jvm.functions.Function0<kotlin.Unit> onEdit, kotlin.jvm.functions.Function0<kotlin.Unit> onDelete) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PresetPromptCard(com.smafty.synapsekeyboard.ui.screens.PresetPrompt preset, int index) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MostActiveCard(com.smafty.synapsekeyboard.data.local.entity.MostUsedPromptEntity prompt, int index, kotlin.jvm.functions.Function0<kotlin.Unit> onResetUsage) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EmptyState(java.lang.String title, java.lang.String subtitle, boolean showAdd, kotlin.jvm.functions.Function0<kotlin.Unit> onAdd) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void PromptBottomSheet(com.smafty.synapsekeyboard.data.local.repository.RemoteCustomPrompt editingPrompt, kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onSave, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
}