package com.smafty.synapsekeyboard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Maintains local AI credits status, synchronized with Supabase.
 * [version] acts as an optimistic locking counter — it auto-increments on every write.
 * The repository layer additionally enforces a [kotlinx.coroutines.sync.Mutex] to prevent
 * concurrent UI triggers from spamming database connections.
 */
@Entity(tableName = "user_credits")
data class UserCreditsEntity(
    @PrimaryKey val userId: String,
    val credits: Int,
    val version: Int = 0
)
