package com.smafty.synapsekeyboard.ui.theme

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.smafty.synapsekeyboard.data.local.SynapseDatabase
import com.smafty.synapsekeyboard.data.local.entity.ThemeSettingsEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Premium minimal theme system — 2 presets only: Premium Black (default) and Premium White.
 * Each preset contains the complete color vocabulary for app screens, keyboard, and overlays.
 */
enum class AppThemePreset(
    val id: String,
    val displayName: String,

    // Core surfaces
    val primary: Color,
    val background: Color,
    val surface: Color,
    val surfaceElevated: Color,
    val border: Color,
    val borderActive: Color,
    val primaryMuted: Color,

    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,

    // Semantic
    val accent: Color,
    val success: Color,
    val error: Color,
    val warning: Color,

    // Keyboard-specific
    val keyboardBg: Color,
    val keyFace: Color,
    val keyFacePressed: Color,
    val keyText: Color,
    val keyBorder: Color,
    val toolbarBg: Color,
    val specialKeyBg: Color,
    val specialKeyIcon: Color,
    val actionKeyBg: Color,

    // Gradient endpoints for primary CTA buttons
    val primaryGradientStart: Color,
    val primaryGradientEnd: Color,

    val isDark: Boolean = true,

    // Legacy compat — previously "secondary" and "muted" were separate fields
    @Deprecated("Use 'accent' instead") val secondary: Color = accent,
    @Deprecated("Use 'textSecondary' instead") val muted: Color = textSecondary
) {
    // ── Premium Black (Default) ─────────────────────────────────────────────
    PREMIUM_BLACK(
        id              = "premium_black",
        displayName     = "Premium Black",
        isDark          = true,

        primary         = PremiumBlackColors.Primary,
        background      = PremiumBlackColors.Background,
        surface         = PremiumBlackColors.Surface,
        surfaceElevated = PremiumBlackColors.SurfaceElevated,
        border          = PremiumBlackColors.Border,
        borderActive    = PremiumBlackColors.BorderActive,
        primaryMuted    = PremiumBlackColors.PrimaryMuted,

        textPrimary     = PremiumBlackColors.TextPrimary,
        textSecondary   = PremiumBlackColors.TextSecondary,
        textTertiary    = PremiumBlackColors.TextTertiary,

        accent          = PremiumBlackColors.Accent,
        success         = PremiumBlackColors.Success,
        error           = PremiumBlackColors.Error,
        warning         = PremiumBlackColors.Warning,

        keyboardBg      = PremiumBlackColors.KeyboardBg,
        keyFace         = PremiumBlackColors.KeyFace,
        keyFacePressed  = PremiumBlackColors.KeyFacePressed,
        keyText         = PremiumBlackColors.KeyText,
        keyBorder       = PremiumBlackColors.KeyBorder,
        toolbarBg       = PremiumBlackColors.ToolbarBg,
        specialKeyBg    = PremiumBlackColors.SpecialKeyBg,
        specialKeyIcon  = PremiumBlackColors.SpecialKeyIcon,
        actionKeyBg     = PremiumBlackColors.ActionKeyBg,

        primaryGradientStart = PremiumBlackColors.Primary,
        primaryGradientEnd   = PremiumBlackColors.PrimaryGradientEnd,

        secondary       = PremiumBlackColors.Accent,
        muted           = PremiumBlackColors.TextSecondary
    ),

    // ── Premium White ────────────────────────────────────────────────────────
    PREMIUM_WHITE(
        id              = "premium_white",
        displayName     = "Premium White",
        isDark          = false,

        primary         = PremiumWhiteColors.Primary,
        background      = PremiumWhiteColors.Background,
        surface         = PremiumWhiteColors.Surface,
        surfaceElevated = PremiumWhiteColors.SurfaceElevated,
        border          = PremiumWhiteColors.Border,
        borderActive    = PremiumWhiteColors.BorderActive,
        primaryMuted    = PremiumWhiteColors.PrimaryMuted,

        textPrimary     = PremiumWhiteColors.TextPrimary,
        textSecondary   = PremiumWhiteColors.TextSecondary,
        textTertiary    = PremiumWhiteColors.TextTertiary,

        accent          = PremiumWhiteColors.Accent,
        success         = PremiumWhiteColors.Success,
        error           = PremiumWhiteColors.Error,
        warning         = PremiumWhiteColors.Warning,

        keyboardBg      = PremiumWhiteColors.KeyboardBg,
        keyFace         = PremiumWhiteColors.KeyFace,
        keyFacePressed  = PremiumWhiteColors.KeyFacePressed,
        keyText         = PremiumWhiteColors.KeyText,
        keyBorder       = PremiumWhiteColors.KeyBorder,
        toolbarBg       = PremiumWhiteColors.ToolbarBg,
        specialKeyBg    = PremiumWhiteColors.SpecialKeyBg,
        specialKeyIcon  = PremiumWhiteColors.SpecialKeyIcon,
        actionKeyBg     = PremiumWhiteColors.ActionKeyBg,

        primaryGradientStart = PremiumWhiteColors.Primary,
        primaryGradientEnd   = PremiumWhiteColors.PrimaryGradientEnd,

        secondary       = PremiumWhiteColors.Accent,
        muted           = PremiumWhiteColors.TextSecondary
    );

    companion object {
        /**
         * Map old theme IDs from the 10-theme system to the new 2-theme system.
         * Any previously-saved dark theme maps → PREMIUM_BLACK.
         * Any previously-saved light theme maps → PREMIUM_WHITE.
         */
        private val legacyIdMap = mapOf(
            // Old dark themes
            "dark_elegance"   to PREMIUM_BLACK,
            "cyberpunk"       to PREMIUM_BLACK,
            "emerald_forest"  to PREMIUM_BLACK,
            "sunset_horizon"  to PREMIUM_BLACK,
            "nordic_frost"    to PREMIUM_BLACK,
            // Old light themes
            "light_sakura"    to PREMIUM_WHITE,
            "ocean_breeze"    to PREMIUM_WHITE,
            "pastel_lavender" to PREMIUM_WHITE,
            "warm_sand"       to PREMIUM_WHITE,
            "mint_fresh"      to PREMIUM_WHITE,
            // New IDs
            "premium_black"   to PREMIUM_BLACK,
            "premium_white"   to PREMIUM_WHITE
        )

        fun fromId(id: String): AppThemePreset {
            return legacyIdMap[id] ?: PREMIUM_BLACK
        }
    }
}

/**
 * Singleton to manage active theme preset, load theme on startup from Room DB,
 * and persist selected theme on changes.
 */
object ThemeManager {
    var currentTheme by mutableStateOf(AppThemePreset.PREMIUM_BLACK)
        private set

    /**
     * Initializes the theme from the offline Room database.
     * Handles migration from old 10-theme IDs to the new 2-theme system.
     * Safe to invoke from any thread; queries are moved to Dispatchers.IO.
     */
    fun initialize(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = SynapseDatabase.getInstance(context)
                val settings = db.themeSettingsDao().getThemeSettings()
                settings?.let {
                    val preset = AppThemePreset.fromId(it.activeThemeId)
                    CoroutineScope(Dispatchers.Main).launch {
                        currentTheme = preset
                    }
                }
            } catch (e: Exception) {
                // Fail-safe default — PREMIUM_BLACK
            }
        }
    }

    /**
     * Switches the active theme preset and saves it to the offline Room database.
     */
    fun selectTheme(context: Context, theme: AppThemePreset) {
        currentTheme = theme
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = SynapseDatabase.getInstance(context)
                db.themeSettingsDao().saveThemeSettings(ThemeSettingsEntity(activeThemeId = theme.id))
            } catch (e: Exception) {
                // Fail-safe write
            }
        }
    }
}
