package com.smafty.synapsekeyboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smafty.synapsekeyboard.data.local.entity.UserCreditsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCreditsDao {

    /** One-shot fetch of credits — use for initial reads or non-reactive checks. */
    @Query("SELECT * FROM user_credits WHERE userId = :userId LIMIT 1")
    suspend fun getUserCredits(userId: String): UserCreditsEntity?

    /** Reactive credits stream — UI observes this for real-time balance updates. */
    @Query("SELECT * FROM user_credits WHERE userId = :userId LIMIT 1")
    fun getUserCreditsFlow(userId: String): Flow<UserCreditsEntity?>

    /** Inserts or fully replaces the credits record for this user. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserCredits(creditsEntity: UserCreditsEntity)

    /**
     * Decrements credits with optimistic locking verification.
     * Only updates if the stored [version] matches [expectedVersion], preventing
     * concurrent race conditions from applying a stale decrement.
     *
     * @return 1 if the update succeeded, 0 if the version check failed (retry required).
     */
    @Query(
        "UPDATE user_credits SET credits = :newCredits, version = version + 1 " +
        "WHERE userId = :userId AND version = :expectedVersion"
    )
    suspend fun decrementCreditsWithLock(
        userId: String,
        newCredits: Int,
        expectedVersion: Int
    ): Int
}
