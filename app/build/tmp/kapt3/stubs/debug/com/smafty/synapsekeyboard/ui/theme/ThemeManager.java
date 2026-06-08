package com.smafty.synapsekeyboard.ui.theme;

/**
 * Singleton to manage active theme preset, load theme on startup from Room DB,
 * and persist selected theme on changes.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000fJ\u0016\u0010\u0010\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u0004R+\u0010\u0005\u001a\u00020\u00042\u0006\u0010\u0003\u001a\u00020\u00048F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\n\u0010\u000b\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\t\u00a8\u0006\u0012"}, d2 = {"Lcom/smafty/synapsekeyboard/ui/theme/ThemeManager;", "", "()V", "<set-?>", "Lcom/smafty/synapsekeyboard/ui/theme/AppThemePreset;", "currentTheme", "getCurrentTheme", "()Lcom/smafty/synapsekeyboard/ui/theme/AppThemePreset;", "setCurrentTheme", "(Lcom/smafty/synapsekeyboard/ui/theme/AppThemePreset;)V", "currentTheme$delegate", "Landroidx/compose/runtime/MutableState;", "initialize", "", "context", "Landroid/content/Context;", "selectTheme", "theme", "app_debug"})
public final class ThemeManager {
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.runtime.MutableState currentTheme$delegate = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.smafty.synapsekeyboard.ui.theme.ThemeManager INSTANCE = null;
    
    private ThemeManager() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.smafty.synapsekeyboard.ui.theme.AppThemePreset getCurrentTheme() {
        return null;
    }
    
    private final void setCurrentTheme(com.smafty.synapsekeyboard.ui.theme.AppThemePreset p0) {
    }
    
    /**
     * Initializes the theme from the offline Room database.
     * Safe to invoke from any thread; queries are moved to Dispatchers.IO.
     */
    public final void initialize(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * Switches the active theme preset and saves it to the offline Room database.
     */
    public final void selectTheme(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.ui.theme.AppThemePreset theme) {
    }
}