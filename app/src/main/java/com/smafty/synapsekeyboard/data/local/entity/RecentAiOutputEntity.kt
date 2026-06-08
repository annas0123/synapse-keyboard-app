package com.smafty.synapsekeyboard.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Caches only the 10 most recent AI-generated outputs for quick copy/paste access
 * from the keyboard panel. Older entries are automatically pruned via
 * [com.smafty.synapsekeyboard.data.local.dao.RecentAiOutputDao.pruneOlderThan10].
 */
@Entity(
    tableName = "recent_ai_outputs",
    indices = [Index("timestamp")]
)
data class RecentAiOutputEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val outputContent: String,
    val timestamp: Long = System.currentTimeMillis()
)
