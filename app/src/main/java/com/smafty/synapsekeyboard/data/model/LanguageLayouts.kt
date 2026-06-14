package com.smafty.synapsekeyboard.data.model

/**
 * Defines the key character matrices for each supported language layout.
 *
 * Each layout provides three rows of main keys (ROW_1, ROW_2, ROW_3),
 * optional long-press hint characters for ROW_1, and a display label for the space bar.
 *
 * NOTE: RTL languages (Urdu, Arabic) use native Unicode characters directly.
 * The Android text framework handles RTL cursor direction automatically.
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
    // Urdu — Standard phonetic/native layout
    // ─────────────────────────────────────────────────────────────────────────
    private val URDU = Layout(
        row1       = listOf("ق","و","ع","ر","ت","ی","ئ","ا","و","پ"),
        row2       = listOf("ا","س","د","ف","گ","ہ","ج","ک","ل"),
        row3       = listOf("ز","ش","چ","ط","ب","ن","م"),
        row1Hints  = listOf("1","2","3","4","5","6","7","8","9","0"),
        spaceLabel = "اردو",
        supportsShift = false
    )

    // ─────────────────────────────────────────────────────────────────────────
    // Arabic — Standard layout
    // ─────────────────────────────────────────────────────────────────────────
    private val ARABIC = Layout(
        row1       = listOf("ض","ص","ث","ق","ف","غ","ع","ه","خ","ح"),
        row2       = listOf("ش","س","ي","ب","ل","ا","ت","ن","م"),
        row3       = listOf("ئ","ء","ؤ","ر","و","ز","ظ"),
        row1Hints  = listOf("1","2","3","4","5","6","7","8","9","0"),
        spaceLabel = "عربي",
        supportsShift = false
    )

    // ─────────────────────────────────────────────────────────────────────────
    // Spanish — QWERTY with Ñ support
    // ─────────────────────────────────────────────────────────────────────────
    private val SPANISH = Layout(
        row1       = listOf("q","w","e","r","t","y","u","i","o","p"),
        row2       = listOf("a","s","d","f","g","h","j","k","l","ñ"),
        row3       = listOf("z","x","c","v","b","n","m"),
        row1Hints  = listOf("1","2","3","4","5","6","7","8","9","0"),
        spaceLabel = "Español"
    )

    // ─────────────────────────────────────────────────────────────────────────
    // Registry: language key → Layout
    // ─────────────────────────────────────────────────────────────────────────
    private val REGISTRY: Map<String, Layout> = mapOf(
        "English" to ENGLISH,
        "Urdu"    to URDU,
        "Arabic"  to ARABIC,
        "Spanish" to SPANISH
    )

    /** All available language keys in display order. */
    val allLanguages: List<String> = listOf("English", "Urdu", "Arabic", "Spanish")

    /** Returns the Layout for the given language key, defaulting to English. */
    fun getLayout(languageKey: String): Layout =
        REGISTRY[languageKey] ?: ENGLISH
}
