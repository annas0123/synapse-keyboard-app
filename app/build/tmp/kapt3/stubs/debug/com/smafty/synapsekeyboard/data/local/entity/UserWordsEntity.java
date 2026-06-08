package com.smafty.synapsekeyboard.data.local.entity;

/**
 * Tracks per-user word quota, synchronized with Supabase `user_word_limits`.
 *
 * Word consumption is measured in tokens × 0.75 (prompt_tokens + completion_tokens from
 * the OpenRouter `usage` object), giving a fair approximation of readable words processed.
 *
 * [wordsAllowed]  — the user's current quota ceiling (default: 20,000 for free accounts).
 * [wordsUsed]     — cumulative words consumed across all AI interactions.
 * [wordsRemaining] — derived: clamped to 0 so UI never shows negatives.
 * [version]       — optimistic locking counter, mirrors the Supabase row version.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B\'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\bJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0005H\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u000e\u001a\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\f\u00a8\u0006\u001b"}, d2 = {"Lcom/smafty/synapsekeyboard/data/local/entity/UserWordsEntity;", "", "userId", "", "wordsAllowed", "", "wordsUsed", "version", "(Ljava/lang/String;III)V", "getUserId", "()Ljava/lang/String;", "getVersion", "()I", "getWordsAllowed", "wordsRemaining", "getWordsRemaining", "getWordsUsed", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "app_debug"})
@androidx.room.Entity(tableName = "user_words")
public final class UserWordsEntity {
    @androidx.room.PrimaryKey()
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String userId = null;
    private final int wordsAllowed = 0;
    private final int wordsUsed = 0;
    private final int version = 0;
    
    public UserWordsEntity(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, int wordsAllowed, int wordsUsed, int version) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUserId() {
        return null;
    }
    
    public final int getWordsAllowed() {
        return 0;
    }
    
    public final int getWordsUsed() {
        return 0;
    }
    
    public final int getVersion() {
        return 0;
    }
    
    public final int getWordsRemaining() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component1() {
        return null;
    }
    
    public final int component2() {
        return 0;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final int component4() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.smafty.synapsekeyboard.data.local.entity.UserWordsEntity copy(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, int wordsAllowed, int wordsUsed, int version) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}