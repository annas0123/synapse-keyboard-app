package com.smafty.synapsekeyboard.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smafty.synapsekeyboard.data.local.entity.ThemeSettingsEntity

/**
 * Data Access Object for local theme settings persistence.
 */
@Dao
interface ThemeSettingsDao {
    @Query("SELECT * FROM theme_settings WHERE id = 1 LIMIT 1")
    suspend fun getThemeSettings(): ThemeSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveThemeSettings(settings: ThemeSettingsEntity)
}
