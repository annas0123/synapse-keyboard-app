package com.smafty.synapsekeyboard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persists the user's active theme preset ID in the offline database.
 * Uses a single-row design (id = 1) to ensure database-level setting constraints.
 */
@Entity(tableName = "theme_settings")
data class ThemeSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val activeThemeId: String
)
