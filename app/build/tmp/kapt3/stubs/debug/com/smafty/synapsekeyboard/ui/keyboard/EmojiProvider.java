package com.smafty.synapsekeyboard.ui.keyboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\b\u0006\n\u0002\u0010$\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0014\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00060\b2\u0006\u0010\"\u001a\u00020\nJ\u000e\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020\u0006J\u000e\u0010&\u001a\u00020\'2\u0006\u0010(\u001a\u00020)J\u0016\u0010*\u001a\u00020\'2\u0006\u0010+\u001a\u00020\u00062\u0006\u0010(\u001a\u00020)J\u0010\u0010,\u001a\u00020\'2\u0006\u0010(\u001a\u00020)H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR!\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\u000e8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u000f\u0010\u0010R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00060\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00060\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00060\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00060\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00060\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00060\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00060\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00060\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00060\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00060\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2 = {"Lcom/smafty/synapsekeyboard/ui/keyboard/EmojiProvider;", "", "()V", "MAX_RECENTS", "", "PREFS_KEY_RECENTS", "", "activities", "", "allCategories", "Lcom/smafty/synapsekeyboard/ui/keyboard/EmojiCategory;", "getAllCategories", "()Ljava/util/List;", "allEmojisSet", "", "getAllEmojisSet", "()Ljava/util/Set;", "allEmojisSet$delegate", "Lkotlin/Lazy;", "animals", "categoryIcons", "", "getCategoryIcons", "()Ljava/util/Map;", "flags", "food", "objects", "people", "recentEmojis", "Lkotlin/collections/ArrayDeque;", "smileys", "symbols", "travel", "getEmojis", "category", "isEmoji", "", "char", "loadRecents", "", "prefs", "Landroid/content/SharedPreferences;", "recordUsed", "emoji", "saveRecents", "app_debug"})
public final class EmojiProvider {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_KEY_RECENTS = "emoji_recents";
    private static final int MAX_RECENTS = 32;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.collections.ArrayDeque<java.lang.String> recentEmojis = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.smafty.synapsekeyboard.ui.keyboard.EmojiCategory> allCategories = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<com.smafty.synapsekeyboard.ui.keyboard.EmojiCategory, java.lang.String> categoryIcons = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> smileys = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> people = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> animals = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> food = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> travel = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> activities = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> objects = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> symbols = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> flags = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.Lazy allEmojisSet$delegate = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.smafty.synapsekeyboard.ui.keyboard.EmojiProvider INSTANCE = null;
    
    private EmojiProvider() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.smafty.synapsekeyboard.ui.keyboard.EmojiCategory> getAllCategories() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<com.smafty.synapsekeyboard.ui.keyboard.EmojiCategory, java.lang.String> getCategoryIcons() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getEmojis(@org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.ui.keyboard.EmojiCategory category) {
        return null;
    }
    
    /**
     * Load the persisted recent emojis from SharedPreferences (call on IME start).
     */
    public final void loadRecents(@org.jetbrains.annotations.NotNull()
    android.content.SharedPreferences prefs) {
    }
    
    /**
     * Record a newly used emoji — LIFO: emoji jumps to position 0.
     * If it was already in the list, it is moved (not duplicated).
     * Persists immediately to SharedPreferences.
     */
    public final void recordUsed(@org.jetbrains.annotations.NotNull()
    java.lang.String emoji, @org.jetbrains.annotations.NotNull()
    android.content.SharedPreferences prefs) {
    }
    
    private final void saveRecents(android.content.SharedPreferences prefs) {
    }
    
    private final java.util.Set<java.lang.String> getAllEmojisSet() {
        return null;
    }
    
    public final boolean isEmoji(@org.jetbrains.annotations.NotNull()
    java.lang.String p0_1526187) {
        return false;
    }
}