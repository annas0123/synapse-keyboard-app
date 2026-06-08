package com.smafty.synapsekeyboard.data.local.dao;

/**
 * DAO for the `user_words` table — the local cache of the Supabase `user_word_limits` row.
 *
 * Architecture contract:
 * - ALL suspend functions MUST be called from Dispatchers.IO (enforced in repository).
 * - [getUserWordsFlow] is the reactive source of truth observed by the Compose UI.
 * - [incrementWordsUsed] is a direct SQL UPDATE and is safe for concurrent calls.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\bg\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00030\b2\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u001e\u0010\t\u001a\u00020\n2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0010\u00a8\u0006\u0011"}, d2 = {"Lcom/smafty/synapsekeyboard/data/local/dao/UserWordsDao;", "", "getUserWords", "Lcom/smafty/synapsekeyboard/data/local/entity/UserWordsEntity;", "userId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getUserWordsFlow", "Lkotlinx/coroutines/flow/Flow;", "incrementWordsUsed", "", "wordsUsedDelta", "", "(Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveUserWords", "wordsEntity", "(Lcom/smafty/synapsekeyboard/data/local/entity/UserWordsEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface UserWordsDao {
    
    /**
     * One-shot fetch — use for initial seed or pre-flight quota checks.
     */
    @androidx.room.Query(value = "SELECT * FROM user_words WHERE userId = :userId LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getUserWords(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.smafty.synapsekeyboard.data.local.entity.UserWordsEntity> $completion);
    
    /**
     * Reactive Flow — Compose UI collects this for live word balance updates.
     */
    @androidx.room.Query(value = "SELECT * FROM user_words WHERE userId = :userId LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.smafty.synapsekeyboard.data.local.entity.UserWordsEntity> getUserWordsFlow(@org.jetbrains.annotations.NotNull()
    java.lang.String userId);
    
    /**
     * Inserts or fully replaces the local quota record for this user.
     */
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveUserWords(@org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.data.local.entity.UserWordsEntity wordsEntity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Atomically adds [wordsUsedDelta] to the accumulated total.
     * Called immediately after a successful OpenRouter API response.
     *
     * @param wordsUsedDelta tokens × 0.75, rounded to nearest Int.
     */
    @androidx.room.Query(value = "UPDATE user_words SET wordsUsed = wordsUsed + :wordsUsedDelta WHERE userId = :userId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object incrementWordsUsed(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, int wordsUsedDelta, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}