package com.smafty.synapsekeyboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smafty.synapsekeyboard.data.local.entity.UserWordsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for the `user_words` table — the local cache of the Supabase `user_word_limits` row.
 *
 * Architecture contract:
 * - ALL suspend functions MUST be called from Dispatchers.IO (enforced in repository).
 * - [getUserWordsFlow] is the reactive source of truth observed by the Compose UI.
 * - [incrementWordsUsed] is a direct SQL UPDATE and is safe for concurrent calls.
 */
@Dao
interface UserWordsDao {

    /** One-shot fetch — use for initial seed or pre-flight quota checks. */
    @Query("SELECT * FROM user_words WHERE userId = :userId LIMIT 1")
    suspend fun getUserWords(userId: String): UserWordsEntity?

    /** Reactive Flow — Compose UI collects this for live word balance updates. */
    @Query("SELECT * FROM user_words WHERE userId = :userId LIMIT 1")
    fun getUserWordsFlow(userId: String): Flow<UserWordsEntity?>

    /** Inserts or fully replaces the local quota record for this user. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserWords(wordsEntity: UserWordsEntity)

    /**
     * Atomically adds [wordsUsedDelta] to the accumulated total.
     * Called immediately after a successful OpenRouter API response.
     *
     * @param wordsUsedDelta tokens × 0.75, rounded to nearest Int.
     */
    @Query("UPDATE user_words SET wordsUsed = wordsUsed + :wordsUsedDelta WHERE userId = :userId")
    suspend fun incrementWordsUsed(userId: String, wordsUsedDelta: Int)
}
