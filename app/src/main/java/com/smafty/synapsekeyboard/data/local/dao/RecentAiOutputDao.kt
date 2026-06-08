package com.smafty.synapsekeyboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.smafty.synapsekeyboard.data.local.entity.RecentAiOutputEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentAiOutputDao {

    /** Returns the 10 most recent AI outputs, newest first. */
    @Query("SELECT * FROM recent_ai_outputs ORDER BY timestamp DESC LIMIT 10")
    fun getRecentOutputsFlow(): Flow<List<RecentAiOutputEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(output: RecentAiOutputEntity): Long

    @Query("DELETE FROM recent_ai_outputs WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM recent_ai_outputs")
    suspend fun clearAll()

    /** Prunes entries beyond the newest 10, keeping DB lean. */
    @Query(
        "DELETE FROM recent_ai_outputs WHERE id NOT IN " +
        "(SELECT id FROM recent_ai_outputs ORDER BY timestamp DESC LIMIT 10)"
    )
    suspend fun pruneOlderThan10()

    /**
     * Inserts a new AI output and automatically prunes the database to keep only the latest 10.
     * Always call this instead of [insert] directly.
     */
    @Transaction
    suspend fun safeInsertAndPrune(content: String) {
        insert(RecentAiOutputEntity(outputContent = content, timestamp = System.currentTimeMillis()))
        pruneOlderThan10()
    }
}
