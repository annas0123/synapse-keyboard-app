package com.smafty.synapsekeyboard.ui.keyboard

import androidx.compose.ui.graphics.Color

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

        keyboardBg       = Color(0xFF0A0A0F),    // True near-black
        keyFaceDefault   = Color(0xFF1A1A28),    // Default key face
        keyFaceDark      = Color(0xFF1C1C2A),    // Special key (shift, backspace)
        keyFacePressed   = Color(0xFF2A2A3A),    // Press state
        keyText          = Color(0xFFE0E0EA),    // Key label text
        keyTextMuted     = Color(0xFF8888A0),    // Muted: spacebar label, hints

        toolbarBg        = Color(0xFF0E0E18),    // Slightly lighter than keyboardBg

        accentGradientStart = Color(0xFF7C5CFC), // Violet primary
        accentGradientEnd   = Color(0xFFA78BFA), // Violet lighter (hover / capslock)
        enterKeyBg       = Color(0xFF7C5CFC),    // Solid violet — no gradient on key

        hasBorder        = true,
        borderColor      = Color(0xFF252535)     // Very subtle 0.5dp border
    ),

    // ── Premium White ─────────────────────────────────────────────────────────
    // Warm white canvas, crisp keys, deeper violet for contrast on light bg.
    PREMIUM_WHITE(
        displayName      = "Premium White",
        isDark           = false,

        keyboardBg       = Color(0xFFF2F2F7),    // iOS-style keyboard grey
        keyFaceDefault   = Color(0xFFFFFFFF),    // Pure white key face
        keyFaceDark      = Color(0xFFE8E8F0),    // Special key (slightly grey)
        keyFacePressed   = Color(0xFFD8D8E8),    // Press state
        keyText          = Color(0xFF1A1A2E),    // Near-black text for contrast
        keyTextMuted     = Color(0xFF6B6B80),    // Muted: spacebar label, hints

        toolbarBg        = Color(0xFFFAFAFA),    // Off-white toolbar

        accentGradientStart = Color(0xFF6C47FF), // Deeper violet (more contrast on white)
        accentGradientEnd   = Color(0xFF8B6FFF), // Lighter violet
        enterKeyBg       = Color(0xFF7C5CFC),    // Same violet brand colour

        hasBorder        = true,
        borderColor      = Color(0xFFDCDCE5)     // Light grey border on white keys
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
