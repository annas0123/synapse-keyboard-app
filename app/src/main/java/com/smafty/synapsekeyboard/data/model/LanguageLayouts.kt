package com.smafty.synapsekeyboard.data.model

/**
 * Defines the key character matrix for the supported keyboard layout.
 *
 * Synapse ships an **English-only** key layout. Translation into other languages
 * (Urdu, Arabic, Spanish, etc.) happens on the AI-output side via prompt presets
 * — there are no native-script key rows. This keeps typing fast and accurate while
 * the AI handles any target language the user asks for.
 *
 * Each layout provides three rows of main keys (ROW_1, ROW_2, ROW_3),
 * optional long-press hint characters for ROW_1, and a display label for the space bar.
 */
object LanguageLayouts {

    data class Layout(
        val row1: List<String>,
        val row2: List<String>,
        val row3: List<String>,
        val row1Hints: List<String>,
        val spaceLabel: String,
        val supportsShift: Boolean = true
    )

    // ─────────────────────────────────────────────────────────────────────────
    // English QWERTY
    // ─────────────────────────────────────────────────────────────────────────
    private val ENGLISH = Layout(
        row1       = listOf("q","w","e","r","t","y","u","i","o","p"),
        row2       = listOf("a","s","d","f","g","h","j","k","l"),
        row3       = listOf("z","x","c","v","b","n","m"),
        row1Hints  = listOf("1","2","3","4","5","6","7","8","9","0"),
        spaceLabel = "English"
    )

    // ─────────────────────────────────────────────────────────────────────────
    // Registry: language key → Layout
    // ─────────────────────────────────────────────────────────────────────────
    private val REGISTRY: Map<String, Layout> = mapOf(
        "English" to ENGLISH
    )

    /** All available language keys in display order. */
    val allLanguages: List<String> = listOf("English")

    /** Returns the Layout for the given language key, defaulting to English. */
    fun getLayout(languageKey: String): Layout =
        REGISTRY[languageKey] ?: ENGLISH
}
