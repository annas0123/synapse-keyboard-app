package com.smafty.synapsekeyboard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local Room cache of the user's custom prompts.
 * Mirrored from Supabase `custom_prompts` table.
 * The [supabaseId] holds the cloud UUID so we can diff and sync accurately.
 */
@Entity(tableName = "custom_prompts")
data class CustomPromptEntity(
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,
    val supabaseId: String = "",   // UUID from Supabase; blank before first sync
    val title: String,
    val prompt: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
