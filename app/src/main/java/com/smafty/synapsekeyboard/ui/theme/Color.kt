package com.smafty.synapsekeyboard.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Premium Black Theme Colors ──────────────────────────────────────────────
object PremiumBlackColors {
    val Background       = Color(0xFF0A0A0F)
    val Surface          = Color(0xFF141420)
    val SurfaceElevated  = Color(0xFF1C1C2A)
    val Border           = Color(0xFF2A2A3A)
    val BorderActive     = Color(0xFF7C5CFC)

    val Primary          = Color(0xFF7C5CFC)
    val PrimaryGradientEnd = Color(0xFFA78BFA)
    val PrimaryMuted     = Color(0x337C5CFC) // ~20% opacity

    val TextPrimary      = Color(0xFFF0F0F5)
    val TextSecondary    = Color(0xFF8888A0)
    val TextTertiary     = Color(0xFF555570)

    val Accent           = Color(0xFFA78BFA)
    val Success          = Color(0xFF34D399)
    val Error            = Color(0xFFF87171)
    val Warning          = Color(0xFFFBBF24)

    // Keyboard-specific
    val KeyboardBg       = Color(0xFF0A0A0F)
    val KeyFace          = Color(0xFF1A1A28)
    val KeyFacePressed   = Color(0xFF2A2A3A)
    val KeyText          = Color(0xFFE0E0EA)
    val KeyBorder        = Color(0xFF252535)
    val ToolbarBg        = Color(0xFF0E0E18)

    // Special keys
    val SpecialKeyBg     = Color(0xFF1C1C2A)
    val SpecialKeyIcon   = Color(0xFF8888A0)
    val ActionKeyBg      = Color(0xFF7C5CFC)
}

// ─── Premium White Theme Colors ──────────────────────────────────────────────
object PremiumWhiteColors {
    val Background       = Color(0xFFFAFAFA)
    val Surface          = Color(0xFFFFFFFF)
    val SurfaceElevated  = Color(0xFFFFFFFF)
    val Border           = Color(0xFFE8E8F0)
    val BorderActive     = Color(0xFF7C5CFC)

    val Primary          = Color(0xFF6C47FF)
    val PrimaryGradientEnd = Color(0xFF8B6FFF)
    val PrimaryMuted     = Color(0x226C47FF) // ~15% opacity

    val TextPrimary      = Color(0xFF1A1A2E)
    val TextSecondary    = Color(0xFF6B6B80)
    val TextTertiary     = Color(0xFF9898B0)

    val Accent           = Color(0xFF6C47FF)
    val Success          = Color(0xFF10B981)
    val Error            = Color(0xFFEF4444)
    val Warning          = Color(0xFFF59E0B)

    // Keyboard-specific
    val KeyboardBg       = Color(0xFFF2F2F7)
    val KeyFace          = Color(0xFFFFFFFF)
    val KeyFacePressed   = Color(0xFFE8E8F0)
    val KeyText          = Color(0xFF1A1A2E)
    val KeyBorder        = Color(0xFFDCDCE5)
    val ToolbarBg        = Color(0xFFFAFAFA)

    // Special keys
    val SpecialKeyBg     = Color(0xFFF0F0F5)
    val SpecialKeyIcon   = Color(0xFF6B6B80)
    val ActionKeyBg      = Color(0xFF7C5CFC)
}

// ─── Dynamic palette — all tokens delegate to the active ThemeManager preset ─
// These aliases keep backward-compatibility with existing screen code.
// "GlassmorphismColor" is now just "SurfaceColor" under the hood (no blur).

val SynapseBackground: Color get() = ThemeManager.currentTheme.background
val SynapseSurface: Color get() = ThemeManager.currentTheme.surface
val SynapseSurfaceElevated: Color get() = ThemeManager.currentTheme.surfaceElevated
val SynapsePrimary: Color get() = ThemeManager.currentTheme.primary
val SynapsePrimaryMuted: Color get() = ThemeManager.currentTheme.primaryMuted
val SynapseAccent: Color get() = ThemeManager.currentTheme.accent
val SynapseBorder: Color get() = ThemeManager.currentTheme.border
val SynapseBorderActive: Color get() = ThemeManager.currentTheme.borderActive

val SynapseTextPrimary: Color get() = ThemeManager.currentTheme.textPrimary
val SynapseTextSecondary: Color get() = ThemeManager.currentTheme.textSecondary
val SynapseTextTertiary: Color get() = ThemeManager.currentTheme.textTertiary

val SynapseSuccess: Color get() = ThemeManager.currentTheme.success
val SynapseError: Color get() = ThemeManager.currentTheme.error
val SynapseWarning: Color get() = ThemeManager.currentTheme.warning

// ─── Backward-compatible aliases (existing screens reference these names) ────
// These map old token names → new theme system so nothing breaks.

val DeepSlate: Color get() = ThemeManager.currentTheme.background
val PitchBlack: Color get() = ThemeManager.currentTheme.surface
val ElectricPurple: Color get() = ThemeManager.currentTheme.primary
val EmeraldGreen: Color get() = ThemeManager.currentTheme.accent   // secondary → accent
val CrispWhite = Color(0xFFF0F0F5) // Near-white, NOT pure white (per spec)
val MutedGrey: Color get() = ThemeManager.currentTheme.textSecondary
// GlassmorphismColor kept as alias → maps to Surface (solid, no blur)
val GlassmorphismColor: Color get() = ThemeManager.currentTheme.surface
val TextColor: Color get() = ThemeManager.currentTheme.textPrimary
