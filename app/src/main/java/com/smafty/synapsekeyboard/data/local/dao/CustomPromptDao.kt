package com.smafty.synapsekeyboard.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smafty.synapsekeyboard.data.local.entity.CustomPromptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomPromptDao {

    /** Observe all custom prompts in insertion order (most recently updated first). */
    @Query("SELECT * FROM custom_prompts ORDER BY updatedAt DESC")
    fun getAllFlow(): Flow<List<CustomPromptEntity>>

    /** One-shot snapshot — used for Supabase sync operations. */
    @Query("SELECT * FROM custom_prompts")
    suspend fun getAll(): List<CustomPromptEntity>

    @Query("SELECT * FROM custom_prompts WHERE localId = :id LIMIT 1")
    suspend fun getById(id: Int): CustomPromptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prompt: CustomPromptEntity): Long

    @Update
    suspend fun update(prompt: CustomPromptEntity)

    @Delete
    suspend fun delete(prompt: CustomPromptEntity)

    @Query("DELETE FROM custom_prompts")
    suspend fun deleteAll()

    /** Replace the entire local cache with a fresh snapshot from Supabase. */
    suspend fun replaceAll(prompts: List<CustomPromptEntity>) {
        deleteAll()
        prompts.forEach { insert(it) }
    }
}
