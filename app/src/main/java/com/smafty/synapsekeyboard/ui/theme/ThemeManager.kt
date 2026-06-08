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
 * 5 premium theme color palettes matching the app's Dark Elegance style.
 */
enum class AppThemePreset(
    val id: String,
    val displayName: String,
    val primary: Color,
    val background: Color,
    val secondary: Color,
    val surface: Color,
    val muted: Color,
    val isDark: Boolean = true
) {
    // ── Dark themes ────────────────────────────────────────────────────────────
    DARK_ELEGANCE(
        // Inspired by: Linear.app — deep indigo on near-black with violet accents
        "dark_elegance", "Midnight Violet",
        primary    = Color(0xFF7C6AF7), // Soft electric violet
        background = Color(0xFF0D0D14), // True off-black
        secondary  = Color(0xFF2DD4BF), // Teal — contrasts perfectly
        surface    = Color(0xFF18182B), // Deep indigo surface
        muted      = Color(0xFF7B829A)  // Muted blue-grey
    ),
    CYBERPUNK(
        // Inspired by: Vercel dark — pitch black with bold neon accents
        "cyberpunk", "Neon Pulse",
        primary    = Color(0xFFE879F9), // Electric magenta-pink
        background = Color(0xFF07070A), // Near-black
        secondary  = Color(0xFF00FECA), // Aqua-green neon
        surface    = Color(0xFF111119), // Very dark surface
        muted      = Color(0xFF6B7280)  // Neutral grey
    ),
    EMERALD_FOREST(
        // Inspired by: Stripe — deep navy with rich emerald
        "emerald_forest", "Emerald Depth",
        primary    = Color(0xFF10B981), // Emerald green
        background = Color(0xFF0A0F1E), // Deep navy-black
        secondary  = Color(0xFFFBBF24), // Warm amber accent
        surface    = Color(0xFF111827), // Graphite navy
        muted      = Color(0xFF6B8F7A)  // Muted sage
    ),
    SUNSET_HORIZON(
        // Inspired by: Raycast — deep purple base with warm sunset tones
        "sunset_horizon", "Sunset Glow",
        primary    = Color(0xFFF97316), // Vivid orange
        background = Color(0xFF0F0A1A), // Deep violet-black
        secondary  = Color(0xFFEC4899), // Hot pink complement
        surface    = Color(0xFF1A1028), // Rich plum surface
        muted      = Color(0xFF8B7FA0)  // Muted purple-grey
    ),
    NORDIC_FROST(
        // Inspired by: GitHub Dark — deep blue with icy cyan
        "nordic_frost", "Arctic Blue",
        primary    = Color(0xFF38BDF8), // Bright sky blue
        background = Color(0xFF09111F), // Near-black navy
        secondary  = Color(0xFFA78BFA), // Soft lavender complement
        surface    = Color(0xFF111D30), // Dark navy surface
        muted      = Color(0xFF64748B)  // Slate blue-grey
    ),
    // ── Light themes ───────────────────────────────────────────────────────────
    LIGHT_SAKURA(
        // Inspired by: Notion — clean white with confident rose
        "light_sakura", "Rose Quartz",
        primary    = Color(0xFFE11D48), // Rose red
        background = Color(0xFFFAFAFC), // Off-white cool
        secondary  = Color(0xFFF43F5E), // Warm rose
        surface    = Color(0xFFF1F5F9), // Light slate surface
        muted      = Color(0xFF64748B), // Slate grey text
        isDark     = false
    ),
    OCEAN_BREEZE(
        // Inspired by: Linear light — airy blue with clean whites
        "ocean_breeze", "Sky Horizon",
        primary    = Color(0xFF2563EB), // Cobalt blue
        background = Color(0xFFF8FAFF), // Almost-white blue tint
        secondary  = Color(0xFF0EA5E9), // Sky blue
        surface    = Color(0xFFEFF6FF), // Light blue-white
        muted      = Color(0xFF475569), // Mid slate
        isDark     = false
    ),
    PASTEL_LAVENDER(
        // Inspired by: Framer — soft purple gradients on white
        "pastel_lavender", "Lavender Mist",
        primary    = Color(0xFF6D28D9), // Deep violet
        background = Color(0xFFFBFAFF), // Warm near-white
        secondary  = Color(0xFF8B5CF6), // Medium violet
        surface    = Color(0xFFF3F0FF), // Lavender tinted surface
        muted      = Color(0xFF6B5B95), // Muted purple
        isDark     = false
    ),
    WARM_SAND(
        // Inspired by: Superhuman — warm amber on cream
        "warm_sand", "Amber Sand",
        primary    = Color(0xFFD97706), // Warm amber
        background = Color(0xFFFFFBF0), // Cream white
        secondary  = Color(0xFF92400E), // Dark amber
        surface    = Color(0xFFFEF3C7), // Pale yellow surface
        muted      = Color(0xFF78716C), // Warm stone
        isDark     = false
    ),
    MINT_FRESH(
        // Inspired by: Clerk — fresh mint on bright white
        "mint_fresh", "Fresh Mint",
        primary    = Color(0xFF059669), // Rich emerald
        background = Color(0xFFF7FFFC), // Almost-white mint
        secondary  = Color(0xFF0D9488), // Teal
        surface    = Color(0xFFECFDF5), // Mint surface
        muted      = Color(0xFF4B7563), // Muted teal-grey
        isDark     = false
    )
}
/**
 * Singleton to manage active theme preset, load theme on startup from Room DB,
 * and persist selected theme on changes.
 */
object ThemeManager {
    var currentTheme by mutableStateOf(AppThemePreset.DARK_ELEGANCE)
        private set

    /**
     * Initializes the theme from the offline Room database.
     * Safe to invoke from any thread; queries are moved to Dispatchers.IO.
     */
    fun initialize(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = SynapseDatabase.getInstance(context)
                val settings = db.themeSettingsDao().getThemeSettings()
                settings?.let {
                    val preset = AppThemePreset.values().firstOrNull { p -> p.id == it.activeThemeId }
                    preset?.let { p ->
                        CoroutineScope(Dispatchers.Main).launch {
                            currentTheme = p
                        }
                    }
                }
            } catch (e: Exception) {
                // Fail-safe default
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
