package com.smafty.synapsekeyboard.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Stores the user's copied text history with automatic indexing for rapid rendering.
 * Content is truncated to 2,000 characters to prevent excessive storage use.
 * Auto-pruned to 50 unpinned items via [com.smafty.synapsekeyboard.data.local.dao.ClipboardDao.pruneOldUnpinned].
 *
 * NOTE: [isPinned] uses @ColumnInfo(name = "is_pinned") so Room generates the snake_case
 * column name that matches the DAO SQL queries (e.g. WHERE is_pinned = 0).
 */
@Entity(
    tableName = "clipboard_history",
    indices = [Index("timestamp")]
)
data class ClipboardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "is_pinned") val isPinned: Boolean = false
)
