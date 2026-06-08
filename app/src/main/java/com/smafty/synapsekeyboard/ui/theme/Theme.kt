package com.smafty.synapsekeyboard.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme get() = darkColorScheme(
    primary = ElectricPurple,
    secondary = EmeraldGreen,
    tertiary = Pink80,
    background = DeepSlate,
    surface = PitchBlack,
    onPrimary = CrispWhite,
    onSecondary = CrispWhite,
    onBackground = if (ThemeManager.currentTheme.isDark) CrispWhite else Color(0xFF0F172A),
    onSurface = if (ThemeManager.currentTheme.isDark) CrispWhite else Color(0xFF0F172A)
)

private val LightColorScheme get() = lightColorScheme(
    primary = ElectricPurple,
    secondary = EmeraldGreen,
    tertiary = Pink40,
    background = CrispWhite,
    surface = CrispWhite,
    onPrimary = CrispWhite,
    onSecondary = CrispWhite,
    onBackground = PitchBlack,
    onSurface = PitchBlack
)

@Composable
fun SynapseKeyboardTheme(
    darkTheme: Boolean = true, // Force Dark Elegance theme
    dynamicColor: Boolean = false, // Disable dynamic colors to keep our premium look
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
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
