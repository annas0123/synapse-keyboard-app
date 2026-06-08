package com.smafty.synapsekeyboard.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J&\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0018\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u0018\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\r2\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0016\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\nH\u00a7@\u00a2\u0006\u0002\u0010\u0011\u00a8\u0006\u0012"}, d2 = {"Lcom/smafty/synapsekeyboard/data/local/dao/UserCreditsDao;", "", "decrementCreditsWithLock", "", "userId", "", "newCredits", "expectedVersion", "(Ljava/lang/String;IILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getUserCredits", "Lcom/smafty/synapsekeyboard/data/local/entity/UserCreditsEntity;", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getUserCreditsFlow", "Lkotlinx/coroutines/flow/Flow;", "saveUserCredits", "", "creditsEntity", "(Lcom/smafty/synapsekeyboard/data/local/entity/UserCreditsEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface UserCreditsDao {
    
    /**
     * One-shot fetch of credits — use for initial reads or non-reactive checks.
     */
    @androidx.room.Query(value = "SELECT * FROM user_credits WHERE userId = :userId LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getUserCredits(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.smafty.synapsekeyboard.data.local.entity.UserCreditsEntity> $completion);
    
    /**
     * Reactive credits stream — UI observes this for real-time balance updates.
     */
    @androidx.room.Query(value = "SELECT * FROM user_credits WHERE userId = :userId LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.smafty.synapsekeyboard.data.local.entity.UserCreditsEntity> getUserCreditsFlow(@org.jetbrains.annotations.NotNull()
    java.lang.String userId);
    
    /**
     * Inserts or fully replaces the credits record for this user.
     */
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveUserCredits(@org.jetbrains.annotations.NotNull()
    com.smafty.synapsekeyboard.data.local.entity.UserCreditsEntity creditsEntity, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Decrements credits with optimistic locking verification.
     * Only updates if the stored [version] matches [expectedVersion], preventing
     * concurrent race conditions from applying a stale decrement.
     *
     * @return 1 if the update succeeded, 0 if the version check failed (retry required).
     */
    @androidx.room.Query(value = "UPDATE user_credits SET credits = :newCredits, version = version + 1 WHERE userId = :userId AND version = :expectedVersion")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object decrementCreditsWithLock(@org.jetbrains.annotations.NotNull()
    java.lang.String userId, int newCredits, int expectedVersion, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}