package com.smafty.synapsekeyboard.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Premium Black Material3 color scheme — maps to our PremiumBlackColors system.
 */
private val PremiumBlackColorScheme get() = darkColorScheme(
    primary       = PremiumBlackColors.Primary,
    secondary     = PremiumBlackColors.Accent,
    tertiary      = PremiumBlackColors.Accent,
    background    = PremiumBlackColors.Background,
    surface       = PremiumBlackColors.Surface,
    surfaceVariant = PremiumBlackColors.SurfaceElevated,
    onPrimary     = PremiumBlackColors.TextPrimary,
    onSecondary   = PremiumBlackColors.TextPrimary,
    onBackground  = PremiumBlackColors.TextPrimary,
    onSurface     = PremiumBlackColors.TextPrimary,
    onSurfaceVariant = PremiumBlackColors.TextSecondary,
    outline       = PremiumBlackColors.Border,
    outlineVariant = PremiumBlackColors.Border,
    error         = PremiumBlackColors.Error,
    onError       = Color.White
)

/**
 * Premium White Material3 color scheme — maps to our PremiumWhiteColors system.
 */
private val PremiumWhiteColorScheme get() = lightColorScheme(
    primary       = PremiumWhiteColors.Primary,
    secondary     = PremiumWhiteColors.Accent,
    tertiary      = PremiumWhiteColors.Accent,
    background    = PremiumWhiteColors.Background,
    surface       = PremiumWhiteColors.Surface,
    surfaceVariant = PremiumWhiteColors.SurfaceElevated,
    onPrimary     = Color.White,
    onSecondary   = Color.White,
    onBackground  = PremiumWhiteColors.TextPrimary,
    onSurface     = PremiumWhiteColors.TextPrimary,
    onSurfaceVariant = PremiumWhiteColors.TextSecondary,
    outline       = PremiumWhiteColors.Border,
    outlineVariant = PremiumWhiteColors.Border,
    error         = PremiumWhiteColors.Error,
    onError       = Color.White
)

@Composable
fun SynapseKeyboardTheme(
    darkTheme: Boolean = ThemeManager.currentTheme.isDark,
    dynamicColor: Boolean = false, // Disabled — keep our premium look
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PremiumBlackColorScheme else PremiumWhiteColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            // Safe cast: when hosted inside an InputMethodService the context
            // is a Service, not an Activity, so the window is unavailable.
            val activity = view.context as? Activity
            if (activity != null) {
                val window = activity.window
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
