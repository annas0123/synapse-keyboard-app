package com.smafty.synapsekeyboard.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Caches only the user's Top 20 most frequently used prompts locally for offline keyboard access.
 * All other prompts are stored in Supabase (cloud). The [promptId] matches the UUID from Supabase
 * so records stay in sync across local and remote storage.
 */
@Entity(
    tableName = "most_used_prompts",
    indices = [Index(value = ["use_count", "timestamp"])]
)
data class MostUsedPromptEntity(
    @PrimaryKey val promptId: String,
    val title: String,
    val promptText: String,
    val category: String,
    @ColumnInfo(name = "use_count") val useCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
