package com.smafty.synapsekeyboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.smafty.synapsekeyboard.data.local.entity.ClipboardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClipboardDao {

    /** Returns all clipboard items — pinned first, then sorted newest first. */
    @Query("SELECT * FROM clipboard_history ORDER BY is_pinned DESC, timestamp DESC")
    fun getAllHistoryFlow(): Flow<List<ClipboardEntity>>

    /** Returns only unpinned items up to [limit], newest first. */
    @Query("SELECT * FROM clipboard_history WHERE is_pinned = 0 ORDER BY timestamp DESC LIMIT :limit")
    fun getUnpinnedHistoryFlow(limit: Int): Flow<List<ClipboardEntity>>

    /** Returns only pinned items, newest first. */
    @Query("SELECT * FROM clipboard_history WHERE is_pinned = 1 ORDER BY timestamp DESC")
    fun getPinnedHistoryFlow(): Flow<List<ClipboardEntity>>

    /** Finds an existing entry by exact content match. */
    @Query("SELECT * FROM clipboard_history WHERE content = :content LIMIT 1")
    suspend fun getByContent(content: String): ClipboardEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: ClipboardEntity): Long

    @Update
    suspend fun update(item: ClipboardEntity)

    @Query("DELETE FROM clipboard_history WHERE id = :id")
    suspend fun deleteById(id: Int)

    /** Deletes all unpinned items — used by the "Clear All" action. */
    @Query("DELETE FROM clipboard_history WHERE is_pinned = 0")
    suspend fun clearUnpinned()

    @Query("UPDATE clipboard_history SET is_pinned = :isPinned WHERE id = :id")
    suspend fun updatePinStatus(id: Int, isPinned: Boolean)

    /**
     * Prunes unpinned history, keeping only the [keepLimit] newest items.
     * Runs automatically after every insert to enforce a soft cap.
     */
    @Query(
        "DELETE FROM clipboard_history WHERE is_pinned = 0 AND id NOT IN " +
        "(SELECT id FROM clipboard_history WHERE is_pinned = 0 ORDER BY timestamp DESC LIMIT :keepLimit)"
    )
    suspend fun pruneOldUnpinned(keepLimit: Int)

    /**
     * Inserts text safely with de-duplication:
     * - If the text already exists, its timestamp is bumped to the top (preserving pin state).
     * - If new, inserts a fresh entry and prunes old unpinned items to [keepHistoryLimit].
     */
    @Transaction
    suspend fun safeInsertWithDeduplication(
        text: String,
        charLimit: Int = 2000,
        keepHistoryLimit: Int = 50
    ) {
        val truncatedText = text.take(charLimit)
        val existingItem = getByContent(truncatedText)

        if (existingItem != null) {
            update(existingItem.copy(timestamp = System.currentTimeMillis()))
        } else {
            insert(
                ClipboardEntity(
                    content = truncatedText,
                    timestamp = System.currentTimeMillis(),
                    isPinned = false
                )
            )
        }
        pruneOldUnpinned(keepHistoryLimit)
    }
}
