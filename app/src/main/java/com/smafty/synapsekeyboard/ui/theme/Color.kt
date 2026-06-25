package com.smafty.synapsekeyboard.ui.theme

import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────────────────────────────────────
// MONOCHROME PALETTE — single source of truth.
// Only 3 colors exist: Black, White, and the grays between them.
// Zero accent colors. Status colors appear ONLY on tiny indicators.
// Change a value here → the entire app updates (everything reads via ThemeManager).
// ─────────────────────────────────────────────────────────────────────────────

// ─── Pure Black Theme (OLED) ─────────────────────────────────────────────────
object PremiumBlackColors {
    val Background       = Color(0xFF000000) // pure OLED black
    val Surface          = Color(0xFF111111) // cards
    val SurfaceElevated  = Color(0xFF1A1A1A) // elevated surface
    val Border           = Color(0xFF222222)
    val BorderActive     = Color(0xFFFFFFFF) // selection border = primary (white)

    val Primary          = Color(0xFFFFFFFF) // buttons / selections
    val PrimaryGradientEnd = Color(0xFFFFFFFF) // solid — no gradient
    val PrimaryMuted     = Color(0x1AFFFFFF) // rgba(255,255,255,0.10)

    val TextPrimary      = Color(0xFFFFFFFF)
    val TextSecondary    = Color(0xFF888888)
    val TextTertiary     = Color(0xFF555555)

    val Accent           = Color(0xFFFFFFFF) // no accent color → maps to primary
    val Success          = Color(0xFF22C55E) // tiny indicators only
    val Error            = Color(0xFFEF4444) // tiny indicators / destructive only
    val Warning          = Color(0xFFF59E0B) // tiny indicators only

    // Keyboard-specific — monochrome
    val KeyboardBg       = Color(0xFF000000)
    val KeyFace          = Color(0xFF1A1A1A)
    val KeyFacePressed   = Color(0xFF333333)
    val KeyText          = Color(0xFFFFFFFF)
    val KeyBorder        = Color(0xFF222222)
    val ToolbarBg        = Color(0xFF000000)

    // Special keys
    val SpecialKeyBg     = Color(0xFF111111)
    val SpecialKeyIcon   = Color(0xFF888888)
    val ActionKeyBg      = Color(0xFFFFFFFF) // primary-filled action key (icon = background)
}

// ─── Pure White Theme (Clean) ────────────────────────────────────────────────
object PremiumWhiteColors {
    val Background       = Color(0xFFFFFFFF) // pure white
    val Surface          = Color(0xFFF5F5F5) // cards
    val SurfaceElevated  = Color(0xFFFAFAFA) // elevated surface
    val Border           = Color(0xFFE5E5E5)
    val BorderActive     = Color(0xFF000000) // selection border = primary (black)

    val Primary          = Color(0xFF000000) // buttons / selections
    val PrimaryGradientEnd = Color(0xFF000000) // solid — no gradient
    val PrimaryMuted     = Color(0x14000000) // rgba(0,0,0,0.08)

    val TextPrimary      = Color(0xFF000000)
    val TextSecondary    = Color(0xFF666666)
    val TextTertiary     = Color(0xFF999999)

    val Accent           = Color(0xFF000000) // no accent color → maps to primary
    val Success          = Color(0xFF22C55E) // tiny indicators only
    val Error            = Color(0xFFEF4444) // tiny indicators / destructive only
    val Warning          = Color(0xFFF59E0B) // tiny indicators only

    // Keyboard-specific — monochrome
    val KeyboardBg       = Color(0xFFF5F5F5)
    val KeyFace          = Color(0xFFFFFFFF)
    val KeyFacePressed   = Color(0xFFE5E5E5)
    val KeyText          = Color(0xFF000000)
    val KeyBorder        = Color(0xFFE5E5E5)
    val ToolbarBg        = Color(0xFFFFFFFF)

    // Special keys
    val SpecialKeyBg     = Color(0xFFEEEEEE)
    val SpecialKeyIcon   = Color(0xFF666666)
    val ActionKeyBg      = Color(0xFF000000) // primary-filled action key (icon = background)
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
val CrispWhite: Color get() = ThemeManager.currentTheme.textPrimary // theme-aware (white on black / black on white)
val MutedGrey: Color get() = ThemeManager.currentTheme.textSecondary
// GlassmorphismColor kept as alias → maps to Surface (solid, no blur)
val GlassmorphismColor: Color get() = ThemeManager.currentTheme.surface
val TextColor: Color get() = ThemeManager.currentTheme.textPrimary
