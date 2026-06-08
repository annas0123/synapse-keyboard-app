package com.smafty.synapsekeyboard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tracks per-user word quota, synchronized with Supabase `user_word_limits`.
 *
 * Word consumption is measured in tokens × 0.75 (prompt_tokens + completion_tokens from
 * the OpenRouter `usage` object), giving a fair approximation of readable words processed.
 *
 * [wordsAllowed]  — the user's current quota ceiling (default: 20,000 for free accounts).
 * [wordsUsed]     — cumulative words consumed across all AI interactions.
 * [wordsRemaining] — derived: clamped to 0 so UI never shows negatives.
 * [version]       — optimistic locking counter, mirrors the Supabase row version.
 */
@Entity(tableName = "user_words")
data class UserWordsEntity(
    @PrimaryKey val userId: String,
    val wordsAllowed: Int,
    val wordsUsed: Int,
    val version: Int = 0
) {
    val wordsRemaining: Int get() = (wordsAllowed - wordsUsed).coerceAtLeast(0)
}
