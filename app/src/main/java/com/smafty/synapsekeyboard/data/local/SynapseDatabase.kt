package com.smafty.synapsekeyboard.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smafty.synapsekeyboard.data.local.dao.ClipboardDao
import com.smafty.synapsekeyboard.data.local.dao.CustomPromptDao
import com.smafty.synapsekeyboard.data.local.dao.MostUsedPromptDao
import com.smafty.synapsekeyboard.data.local.dao.RecentAiOutputDao
import com.smafty.synapsekeyboard.data.local.dao.UserCreditsDao
import com.smafty.synapsekeyboard.data.local.dao.UserWordsDao
import com.smafty.synapsekeyboard.data.local.entity.ClipboardEntity
import com.smafty.synapsekeyboard.data.local.entity.CustomPromptEntity
import com.smafty.synapsekeyboard.data.local.entity.MostUsedPromptEntity
import com.smafty.synapsekeyboard.data.local.entity.RecentAiOutputEntity
import com.smafty.synapsekeyboard.data.local.entity.UserCreditsEntity
import com.smafty.synapsekeyboard.data.local.entity.UserWordsEntity

import com.smafty.synapsekeyboard.data.local.dao.ThemeSettingsDao
import com.smafty.synapsekeyboard.data.local.entity.ThemeSettingsEntity

/**
 * SynapseDatabase — the single Room database instance for all local offline storage.
 *
 * Architecture rules enforced here:
 * - Version 2: added CustomPromptEntity for user custom prompts (blueprint 27).
 * - Version 3: added UserWordsEntity for token-accurate word quota tracking (blueprint 26).
 *              UserCreditsEntity is kept for migration safety but superseded by UserWordsEntity.
 * - Version 4: added ThemeSettingsEntity for local offline theme selection persistence.
 * - Thread-safe singleton via double-checked locking (@Volatile + synchronized).
 * - All DAO calls MUST be invoked from Dispatchers.IO (enforced in repository layer).
 * - Do NOT call getInstance() on the main thread.
 */
@Database(
    entities = [
        ClipboardEntity::class,
        CustomPromptEntity::class,
        MostUsedPromptEntity::class,
        UserCreditsEntity::class,
        UserWordsEntity::class,
        RecentAiOutputEntity::class,
        ThemeSettingsEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class SynapseDatabase : RoomDatabase() {

    abstract fun clipboardDao(): ClipboardDao
    abstract fun customPromptDao(): CustomPromptDao
    abstract fun mostUsedPromptDao(): MostUsedPromptDao
    abstract fun userCreditsDao(): UserCreditsDao
    abstract fun userWordsDao(): UserWordsDao
    abstract fun recentAiOutputDao(): RecentAiOutputDao
    abstract fun themeSettingsDao(): ThemeSettingsDao

    companion object {
        private const val DATABASE_NAME = "synapse_keyboard_db"

        @Volatile
        private var INSTANCE: SynapseDatabase? = null

        /**
         * Returns the singleton [SynapseDatabase] instance, creating it if necessary.
         * Safe to call from any thread; uses double-checked locking.
         */
        fun getInstance(context: Context): SynapseDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SynapseDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // Safe during development
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
