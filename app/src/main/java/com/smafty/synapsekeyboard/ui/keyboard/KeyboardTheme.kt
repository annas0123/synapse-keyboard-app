package com.smafty.synapsekeyboard.ui.keyboard

import androidx.compose.ui.graphics.Color
import com.smafty.synapsekeyboard.ui.theme.PremiumBlackColors
import com.smafty.synapsekeyboard.ui.theme.PremiumWhiteColors

/**
 * KeyboardTheme — 2-preset system aligned with the premium minimal redesign.
 *
 * Replaces the old 15-theme system. Only PREMIUM_BLACK and PREMIUM_WHITE exist.
 * All legacy theme names are mapped to one of these two via [fromPrefs].
 *
 * NOTE: Only color data is modified here. All logic in SynapseInputMethodService,
 *       KeyboardView composables, and KeyboardUiState is unchanged.
 */
enum class KeyboardTheme(
    val displayName: String,
    // Keyboard surfaces
    val keyboardBg: Color,
    val keyFaceDefault: Color,      // Regular alpha key background
    val keyFaceDark: Color,         // Special keys (shift, backspace, func)
    val keyFacePressed: Color,      // Key pressed state
    // Text
    val keyText: Color,
    val keyTextMuted: Color,
    // Toolbar
    val toolbarBg: Color,
    // Accent — used for active states, Enter key, AI icon active tint
    val accentGradientStart: Color,
    val accentGradientEnd: Color,
    // Enter key is solid (not gradient) per spec
    val enterKeyBg: Color,
    // Border — subtle, 0.5dp
    val isDark: Boolean,
    val hasBorder: Boolean,
    val borderColor: Color,
) {
    // ── Premium Black (Default) ───────────────────────────────────────────────
    // OLED-optimised, near-black base with violet accent. NO glassmorphism.
    PREMIUM_BLACK(
        displayName      = "Premium Black",
        isDark           = true,

        keyboardBg       = PremiumBlackColors.KeyboardBg,     // Pure OLED black
        keyFaceDefault   = PremiumBlackColors.KeyFace,        // Default key face
        keyFaceDark      = PremiumBlackColors.SpecialKeyBg,   // Special key (shift, backspace)
        keyFacePressed   = PremiumBlackColors.KeyFacePressed, // Press state
        keyText          = PremiumBlackColors.KeyText,        // Key label text (white)
        keyTextMuted     = PremiumBlackColors.SpecialKeyIcon, // Muted grey

        toolbarBg        = PremiumBlackColors.ToolbarBg,

        // Monochrome: "accent" / active states are the primary (white).
        accentGradientStart = PremiumBlackColors.Primary,
        accentGradientEnd   = PremiumBlackColors.Primary,
        enterKeyBg       = PremiumBlackColors.ActionKeyBg,    // Solid white action key

        hasBorder        = true,
        borderColor      = PremiumBlackColors.KeyBorder
    ),

    // ── Premium White ─────────────────────────────────────────────────────────
    PREMIUM_WHITE(
        displayName      = "Premium White",
        isDark           = false,

        keyboardBg       = PremiumWhiteColors.KeyboardBg,     // Light grey canvas
        keyFaceDefault   = PremiumWhiteColors.KeyFace,        // Pure white key face
        keyFaceDark      = PremiumWhiteColors.SpecialKeyBg,   // Special key (slightly grey)
        keyFacePressed   = PremiumWhiteColors.KeyFacePressed, // Press state
        keyText          = PremiumWhiteColors.KeyText,        // Near-black text
        keyTextMuted     = PremiumWhiteColors.SpecialKeyIcon, // Muted grey

        toolbarBg        = PremiumWhiteColors.ToolbarBg,

        // Monochrome: "accent" / active states are the primary (black).
        accentGradientStart = PremiumWhiteColors.Primary,
        accentGradientEnd   = PremiumWhiteColors.Primary,
        enterKeyBg       = PremiumWhiteColors.ActionKeyBg,    // Solid black action key

        hasBorder        = true,
        borderColor      = PremiumWhiteColors.KeyBorder
    );

    companion object {
        /**
         * Loads a KeyboardTheme from a SharedPreferences string.
         * Handles both new IDs ("premium_black", "premium_white") and all legacy
         * theme names from the old 15-theme system.
         *
         * Dark legacy themes → PREMIUM_BLACK.
         * Light legacy themes → PREMIUM_WHITE.
         * Unknown → PREMIUM_BLACK.
         */
        fun fromPrefs(value: String?): KeyboardTheme {
            if (value == null) return PREMIUM_BLACK
            return when (value.lowercase()) {
                // New canonical names
                "premium_black",
                "PREMIUM_BLACK"           -> PREMIUM_BLACK

                "premium_white",
                "PREMIUM_WHITE"           -> PREMIUM_WHITE

                // Old dark theme names — map to PREMIUM_BLACK
                "dark_elegance",
                "DARK_ELEGANCE",
                "glassmorphism_frost",
                "GLASSMORPHISM_FROST",
                "cyberpunk_neon",
                "CYBERPUNK_NEON",
                "sunset_horizon",
                "SUNSET_HORIZON",
                "emerald_matrix",
                "EMERALD_MATRIX",
                "royal_amethyst",
                "ROYAL_AMETHYST",
                "magma_core",
                "MAGMA_CORE",
                "sakura_midnight",
                "SAKURA_MIDNIGHT",
                "deep_ocean_abyss",
                "DEEP_OCEAN_ABYSS",
                "vulcan_lava",
                "VULCAN_LAVA"             -> PREMIUM_BLACK

                // Old light theme names — map to PREMIUM_WHITE
                "minimalist_light",
                "MINIMALIST_LIGHT",
                "arctic_glacier",
                "ARCTIC_GLACIER",
                "vintage_sakura",
                "VINTAGE_SAKURA",
                "premium_off_white",
                "PREMIUM_OFF_WHITE"       -> PREMIUM_WHITE

                else                       -> PREMIUM_BLACK
            }
        }
    }
}
