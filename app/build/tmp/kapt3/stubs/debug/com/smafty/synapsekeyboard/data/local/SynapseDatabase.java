package com.smafty.synapsekeyboard.data.local;

/**
 * SynapseDatabase — the single Room database instance for all local offline storage.
 *
 * Architecture rules enforced here:
 * - Version 2: added CustomPromptEntity for user custom prompts (blueprint 27).
 * - Version 3: added UserWordsEntity for token-accurate word quota tracking (blueprint 26).
 *             UserCreditsEntity is kept for migration safety but superseded by UserWordsEntity.
 * - Version 4: added ThemeSettingsEntity for local offline theme selection persistence.
 * - Thread-safe singleton via double-checked locking (@Volatile + synchronized).
 * - All DAO calls MUST be invoked from Dispatchers.IO (enforced in repository layer).
 * - Do NOT call getInstance() on the main thread.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \u00112\u00020\u0001:\u0001\u0011B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0006H&J\b\u0010\u0007\u001a\u00020\bH&J\b\u0010\t\u001a\u00020\nH&J\b\u0010\u000b\u001a\u00020\fH&J\b\u0010\r\u001a\u00020\u000eH&J\b\u0010\u000f\u001a\u00020\u0010H&\u00a8\u0006\u0012"}, d2 = {"Lcom/smafty/synapsekeyboard/data/local/SynapseDatabase;", "Landroidx/room/RoomDatabase;", "()V", "clipboardDao", "Lcom/smafty/synapsekeyboard/data/local/dao/ClipboardDao;", "customPromptDao", "Lcom/smafty/synapsekeyboard/data/local/dao/CustomPromptDao;", "mostUsedPromptDao", "Lcom/smafty/synapsekeyboard/data/local/dao/MostUsedPromptDao;", "recentAiOutputDao", "Lcom/smafty/synapsekeyboard/data/local/dao/RecentAiOutputDao;", "themeSettingsDao", "Lcom/smafty/synapsekeyboard/data/local/dao/ThemeSettingsDao;", "userCreditsDao", "Lcom/smafty/synapsekeyboard/data/local/dao/UserCreditsDao;", "userWordsDao", "Lcom/smafty/synapsekeyboard/data/local/dao/UserWordsDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.smafty.synapsekeyboard.data.local.entity.ClipboardEntity.class, com.smafty.synapsekeyboard.data.local.entity.CustomPromptEntity.class, com.smafty.synapsekeyboard.data.local.entity.MostUsedPromptEntity.class, com.smafty.synapsekeyboard.data.local.entity.UserCreditsEntity.class, com.smafty.synapsekeyboard.data.local.entity.UserWordsEntity.class, com.smafty.synapsekeyboard.data.local.entity.RecentAiOutputEntity.class, com.smafty.synapsekeyboard.data.local.entity.ThemeSettingsEntity.class}, version = 4, exportSchema = false)
public abstract class SynapseDatabase extends androidx.room.RoomDatabase {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DATABASE_NAME = "synapse_keyboard_db";
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.smafty.synapsekeyboard.data.local.SynapseDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.smafty.synapsekeyboard.data.local.SynapseDatabase.Companion Companion = null;
    
    public SynapseDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.smafty.synapsekeyboard.data.local.dao.ClipboardDao clipboardDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.smafty.synapsekeyboard.data.local.dao.CustomPromptDao customPromptDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.smafty.synapsekeyboard.data.local.dao.MostUsedPromptDao mostUsedPromptDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.smafty.synapsekeyboard.data.local.dao.UserCreditsDao userCreditsDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.smafty.synapsekeyboard.data.local.dao.UserWordsDao userWordsDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.smafty.synapsekeyboard.data.local.dao.RecentAiOutputDao recentAiOutputDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.smafty.synapsekeyboard.data.local.dao.ThemeSettingsDao themeSettingsDao();
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0007\u001a\u00020\u00062\u0006\u0010\b\u001a\u00020\tR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\n"}, d2 = {"Lcom/smafty/synapsekeyboard/data/local/SynapseDatabase$Companion;", "", "()V", "DATABASE_NAME", "", "INSTANCE", "Lcom/smafty/synapsekeyboard/data/local/SynapseDatabase;", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * Returns the singleton [SynapseDatabase] instance, creating it if necessary.
         * Safe to call from any thread; uses double-checked locking.
         */
        @org.jetbrains.annotations.NotNull()
        public final com.smafty.synapsekeyboard.data.local.SynapseDatabase getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}