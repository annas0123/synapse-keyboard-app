package com.smafty.synapsekeyboard.ui.keyboard

import androidx.compose.ui.graphics.Color

/**
 * KeyboardTheme — Enum defining premium styling tokens for the 4 theme presets.
 */
enum class KeyboardTheme(
    val displayName: String,
    val keyboardBg: Color,
    val keyFaceDefault: Color,
    val keyFaceDark: Color,
    val keyText: Color,
    val keyTextMuted: Color,
    val toolbarBg: Color,
    val accentGradientStart: Color,
    val accentGradientEnd: Color,
    val isDark: Boolean,
    val hasBorder: Boolean,
    val borderColor: Color
) {
    DARK_ELEGANCE(
        displayName = "Dark Elegance",
        keyboardBg = Color(0xFF0D1526),
        keyFaceDefault = Color(0xFF1E293B),
        keyFaceDark = Color(0xFF111827),
        keyText = Color(0xFFFFFFFF),
        keyTextMuted = Color(0xFF94A3B8),
        toolbarBg = Color(0xFF0B1120),
        accentGradientStart = Color(0xFF8B5CF6), // Electric Purple
        accentGradientEnd = Color(0xFF10B981),   // Emerald Green
        isDark = true,
        hasBorder = false,
        borderColor = Color.Transparent
    ),

    GLASSMORPHISM_FROST(
        displayName = "Glassmorphism Frost",
        keyboardBg = Color(0xFF0D1222),
        keyFaceDefault = Color(0x33FFFFFF), // Frosted white
        keyFaceDark = Color(0x59FFFFFF),    // Slightly less transparent
        keyText = Color(0xFFFFFFFF),
        keyTextMuted = Color(0xFFCBD5E1),
        toolbarBg = Color(0x1F000000),
        accentGradientStart = Color(0xFF06B6D4), // Neon Cyan
        accentGradientEnd = Color(0xFF3B82F6),   // Frost Blue
        isDark = true,
        hasBorder = true,
        borderColor = Color(0x1AFFFFFF)
    ),

    CYBERPUNK_NEON(
        displayName = "Cyberpunk Neon",
        keyboardBg = Color(0xFF000000),      // Pure black
        keyFaceDefault = Color(0xFF121212),  // Charcoal
        keyFaceDark = Color(0xFF050505),     // Deep charcoal
        keyText = Color(0xFF06B6D4),         // Cyan glow text
        keyTextMuted = Color(0xFFEC4899),    // Hot Pink muted
        toolbarBg = Color(0xFF080808),
        accentGradientStart = Color(0xFFEC4899), // Hot Pink
        accentGradientEnd = Color(0xFF8B5CF6),   // Neon Purple
        isDark = true,
        hasBorder = true,
        borderColor = Color(0xFF06B6D4)      // Cyan borders
    ),

    MINIMALIST_LIGHT(
        displayName = "Minimalist Light",
        keyboardBg = Color(0xFFF3F4F6),      // Cool light grey
        keyFaceDefault = Color(0xFFFFFFFF),  // Pure White
        keyFaceDark = Color(0xFFE5E7EB),     // Muted grey
        keyText = Color(0xFF1F2937),         // Charcoal text
        keyTextMuted = Color(0xFF6B7280),    // Muted slate text
        toolbarBg = Color(0xFFE5E7EB),
        accentGradientStart = Color(0xFF4B5563), // Slate grey
        accentGradientEnd = Color(0xFF9CA3AF),   // Light slate
        isDark = false,
        hasBorder = true,
        borderColor = Color(0xFFD1D5DB)      // Soft grey border
    ),

    SUNSET_HORIZON(
        displayName = "Sunset Horizon",
        keyboardBg = Color(0xFF1E1B4B),
        keyFaceDefault = Color(0x26EC4899),
        keyFaceDark = Color(0x40EC4899),
        keyText = Color(0xFFFDE047),
        keyTextMuted = Color(0xFFF472B6),
        toolbarBg = Color(0xFF11103C),
        accentGradientStart = Color(0xFFF97316),
        accentGradientEnd = Color(0xFFEC4899),
        isDark = true,
        hasBorder = true,
        borderColor = Color(0x338B5CF6)
    ),

    EMERALD_MATRIX(
        displayName = "Emerald Matrix",
        keyboardBg = Color(0xFF000000),
        keyFaceDefault = Color(0xFF0A0A0A),
        keyFaceDark = Color(0xFF141414),
        keyText = Color(0xFF10B981),
        keyTextMuted = Color(0xFF059669),
        toolbarBg = Color(0xFF050505),
        accentGradientStart = Color(0xFF34D399),
        accentGradientEnd = Color(0xFF06B6D4),
        isDark = true,
        hasBorder = true,
        borderColor = Color(0x4D10B981)
    ),

    ROYAL_AMETHYST(
        displayName = "Royal Amethyst",
        keyboardBg = Color(0xFF1A0933),
        keyFaceDefault = Color(0xFF2D124D),
        keyFaceDark = Color(0xFF1F0B36),
        keyText = Color(0xFFE9D5FF),
        keyTextMuted = Color(0xFFC084FC),
        toolbarBg = Color(0xFF120524),
        accentGradientStart = Color(0xFFF59E0B),
        accentGradientEnd = Color(0xFFD97706),
        isDark = true,
        hasBorder = false,
        borderColor = Color.Transparent
    ),

    ARCTIC_GLACIER(
        displayName = "Arctic Glacier",
        keyboardBg = Color(0xFFE0F2FE),
        keyFaceDefault = Color(0xFFFFFFFF),
        keyFaceDark = Color(0xFFD0E3F7),
        keyText = Color(0xFF1E3A8A),
        keyTextMuted = Color(0xFF0284C7),
        toolbarBg = Color(0xFFD0E8FF),
        accentGradientStart = Color(0xFF2DD4BF),
        accentGradientEnd = Color(0xFF3B82F6),
        isDark = false,
        hasBorder = true,
        borderColor = Color(0xFFBAE6FD)
    ),

    VINTAGE_SAKURA(
        displayName = "Vintage Sakura",
        keyboardBg = Color(0xFFFFF1F2),
        keyFaceDefault = Color(0xFFFFFFFF),
        keyFaceDark = Color(0xFFFFE4E6),
        keyText = Color(0xFF9F1239),
        keyTextMuted = Color(0xFFE11D48),
        toolbarBg = Color(0xFFFFF0F1),
        accentGradientStart = Color(0xFFFB7185),
        accentGradientEnd = Color(0xFFFDBA74),
        isDark = false,
        hasBorder = true,
        borderColor = Color(0xFFFECDD3)
    ),

    MAGMA_CORE(
        displayName = "Magma Core",
        keyboardBg = Color(0xFF0F0F0F),
        keyFaceDefault = Color(0xFF1C1917),
        keyFaceDark = Color(0xFF0C0A09),
        keyText = Color(0xFFF97316),
        keyTextMuted = Color(0xFFEF4444),
        toolbarBg = Color(0xFF080808),
        accentGradientStart = Color(0xFFDC2626),
        accentGradientEnd = Color(0xFFEA580C),
        isDark = true,
        hasBorder = true,
        borderColor = Color(0x66EF4444)
    ),

    PREMIUM_OFF_WHITE(
        displayName = "Premium Off-White",
        keyboardBg = Color(0xFFF8F8FA),      // Off-white canvas
        keyFaceDefault = Color(0xFFFFFFFF),  // Pure white keycaps
        keyFaceDark = Color(0xFFEAEAEF),     // Functional key bg (shift, del, etc.)
        keyText = Color(0xFF2D2D30),         // Charcoal text
        keyTextMuted = Color(0xFF8A8A8E),    // Muted grey for hints
        toolbarBg = Color(0xFFF1F1F4),       // Subtle off-white toolbar
        accentGradientStart = Color(0xFFFF3131), // Crimson Red
        accentGradientEnd = Color(0xFFFF914D),   // Sunset Orange
        isDark = false,
        hasBorder = true,
        borderColor = Color(0xFFE4E4E7)      // Light, precise key borders
    ),

    SAKURA_MIDNIGHT(
        displayName = "Sakura Midnight",
        keyboardBg = Color(0xFF160E18),      // Deep midnight plum background
        keyFaceDefault = Color(0xFF2B1C2E),  // Prominent plum keycaps
        keyFaceDark = Color(0xFF1E1120),     // Dark plum functional keycaps
        keyText = Color(0xFFFCE7F3),         // Soft blossom-white text
        keyTextMuted = Color(0xFFEC4899),    // Rose/magenta secondary text
        toolbarBg = Color(0xFF0F0811),       // Very dark toolbar
        accentGradientStart = Color(0xFFEC4899), // Neon Rose
        accentGradientEnd = Color(0xFFF43F5E),   // Radiant Coral
        isDark = true,
        hasBorder = false,                   // No border outline
        borderColor = Color.Transparent
    ),

    DEEP_OCEAN_ABYSS(
        displayName = "Deep Ocean Abyss",
        keyboardBg = Color(0xFF040F16),      // Deep abyss blue-black background
        keyFaceDefault = Color(0xFF0F2633),  // Prominent cyan-teal keycaps
        keyFaceDark = Color(0xFF081720),     // Darker navy functional keycaps
        keyText = Color(0xFFE0F2FE),         // Ice blue key text
        keyTextMuted = Color(0xFF38BDF8),    // Muted ocean blue secondary text
        toolbarBg = Color(0xFF02090D),       // Darker ocean toolbar
        accentGradientStart = Color(0xFF06B6D4), // Cyan Glow
        accentGradientEnd = Color(0xFF14B8A6),   // Teal Shimmer
        isDark = true,
        hasBorder = false,                   // No border outline
        borderColor = Color.Transparent
    ),

    VULCAN_LAVA(
        displayName = "Vulcan Lava",
        keyboardBg = Color(0xFF060302),      // Volcanic dark charcoal background
        keyFaceDefault = Color(0xFF220E06),  // Prominent warm amber-charcoal keycaps
        keyFaceDark = Color(0xFF120603),     // Deep dark functional keycaps
        keyText = Color(0xFFFFD8C4),         // Radiant sunset-peach text
        keyTextMuted = Color(0xFFF97316),    // Fire orange secondary text
        toolbarBg = Color(0xFF030101),       // Charcoal black toolbar
        accentGradientStart = Color(0xFFF97316), // Electric Orange
        accentGradientEnd = Color(0xFFEF4444),   // Crimson Red
        isDark = true,
        hasBorder = false,                   // No border outline
        borderColor = Color.Transparent
    )
}
