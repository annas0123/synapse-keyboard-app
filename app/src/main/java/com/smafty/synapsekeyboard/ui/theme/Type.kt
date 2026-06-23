package com.smafty.synapsekeyboard.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp

// Google Fonts provider — certificates fetched automatically from GMS
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage   = "com.google.android.gms",
    certificates      = emptyList<List<ByteArray>>()
)

private val OutfitFont = GoogleFont("Outfit")

val OutfitFamily = FontFamily(
    Font(googleFont = OutfitFont, fontProvider = provider, weight = FontWeight.Normal),   // 400
    Font(googleFont = OutfitFont, fontProvider = provider, weight = FontWeight.Medium),    // 500
    Font(googleFont = OutfitFont, fontProvider = provider, weight = FontWeight.SemiBold),  // 600
    Font(googleFont = OutfitFont, fontProvider = provider, weight = FontWeight.Bold),      // 700
)

/**
 * Premium type scale aligned with the redesign spec.
 *
 * Mapping to design tokens:
 *   Screen Title     → headlineSmall   (Bold 24sp)
 *   Section Header   → labelMedium     (SemiBold 13sp, uppercase, letterSpacing 0.5sp)
 *   Card Title       → titleMedium     (Medium 16sp)
 *   Card Subtitle    → bodySmall       (Regular 13sp)
 *   Body             → bodyLarge       (Regular 15sp)
 *   Caption          → labelSmall      (Regular 12sp)
 *   Button           → labelLarge      (Medium 15sp)
 *   Tab Label        → titleSmall      (Medium 14sp)
 *   Keyboard Key     → titleLarge      (Medium 20sp)  — used in KeyboardView
 *   Keyboard Special → bodyMedium      (Medium 16sp)  — used in KeyboardView
 */
val Typography = Typography(
    // Headlines — used sparingly for large screen titles
    headlineLarge = TextStyle(
        fontFamily    = OutfitFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 28.sp,
        lineHeight    = 36.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineMedium = TextStyle(
        fontFamily    = OutfitFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 26.sp,
        lineHeight    = 34.sp,
        letterSpacing = (-0.15).sp
    ),
    // Screen Title: Outfit Bold 24sp
    headlineSmall = TextStyle(
        fontFamily    = OutfitFamily,
        fontWeight    = FontWeight.Bold,
        fontSize      = 24.sp,
        lineHeight    = 32.sp,
        letterSpacing = 0.sp
    ),

    // Keyboard Key: Outfit Medium 20sp (used for key labels)
    titleLarge = TextStyle(
        fontFamily    = OutfitFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 20.sp,
        lineHeight    = 28.sp,
        letterSpacing = 0.sp
    ),
    // Card Title: Outfit Medium 16sp
    titleMedium = TextStyle(
        fontFamily    = OutfitFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 16.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.15.sp
    ),
    // Tab Label: Outfit Medium 14sp
    titleSmall = TextStyle(
        fontFamily    = OutfitFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 14.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body: Outfit Regular 15sp
    bodyLarge = TextStyle(
        fontFamily    = OutfitFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 15.sp,
        lineHeight    = 22.sp,
        letterSpacing = 0.25.sp
    ),
    // Keyboard Special Key: Outfit Medium 16sp (for special key labels)
    bodyMedium = TextStyle(
        fontFamily    = OutfitFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 16.sp,
        lineHeight    = 24.sp,
        letterSpacing = 0.25.sp
    ),
    // Card Subtitle: Outfit Regular 13sp
    bodySmall = TextStyle(
        fontFamily    = OutfitFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 13.sp,
        lineHeight    = 18.sp,
        letterSpacing = 0.4.sp
    ),

    // Button: Outfit Medium 15sp
    labelLarge = TextStyle(
        fontFamily    = OutfitFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 15.sp,
        lineHeight    = 20.sp,
        letterSpacing = 0.1.sp
    ),
    // Section Header: Outfit SemiBold 13sp (uppercase, letter-spacing 0.5sp)
    labelMedium = TextStyle(
        fontFamily    = OutfitFamily,
        fontWeight    = FontWeight.SemiBold,
        fontSize      = 13.sp,
        lineHeight    = 18.sp,
        letterSpacing = 0.5.sp
    ),
    // Caption: Outfit Regular 12sp
    labelSmall = TextStyle(
        fontFamily    = OutfitFamily,
        fontWeight    = FontWeight.Normal,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.5.sp
    ),
)
