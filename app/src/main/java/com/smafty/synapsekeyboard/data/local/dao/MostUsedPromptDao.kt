package com.smafty.synapsekeyboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.smafty.synapsekeyboard.data.local.entity.MostUsedPromptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MostUsedPromptDao {

    /**
     * Returns the Top 20 most frequently used prompts sorted by usage count descending,
     * then by recency. Used to populate the offline AI prompts panel.
     */
    @Query("SELECT * FROM most_used_prompts ORDER BY use_count DESC, timestamp DESC LIMIT 20")
    fun getTop20PromptsFlow(): Flow<List<MostUsedPromptEntity>>

    @Query("SELECT * FROM most_used_prompts WHERE promptId = :promptId LIMIT 1")
    suspend fun getById(promptId: String): MostUsedPromptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prompt: MostUsedPromptEntity)

    @Update
    suspend fun update(prompt: MostUsedPromptEntity)

    @Query("DELETE FROM most_used_prompts WHERE promptId = :promptId")
    suspend fun deleteById(promptId: String)

    /**
     * Automatically keeps the local database pruned to a strict ceiling of 20 items.
     * Deletes any rows that fall outside the Top 20 usage list.
     */
    @Query(
        "DELETE FROM most_used_prompts WHERE promptId NOT IN " +
        "(SELECT promptId FROM most_used_prompts ORDER BY use_count DESC, timestamp DESC LIMIT 20)"
    )
    suspend fun pruneBelowTop20()

    @Query("DELETE FROM most_used_prompts")
    suspend fun deleteAll()

    /**
     * Increments usage of a prompt. If the prompt does not exist in local SQLite, inserts it.
     * Automatically prunes the table to keep only the Top 20 offline entries.
     */
    @Transaction
    suspend fun incrementPromptUsage(
        promptId: String,
        title: String,
        promptText: String,
        category: String
    ) {
        val existing = getById(promptId)
        if (existing != null) {
            update(
                existing.copy(
                    useCount = existing.useCount + 1,
                    timestamp = System.currentTimeMillis()
                )
            )
        } else {
            insert(
                MostUsedPromptEntity(
                    promptId = promptId,
                    title = title,
                    promptText = promptText,
                    category = category,
                    useCount = 1,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
        pruneBelowTop20()
    }
}
