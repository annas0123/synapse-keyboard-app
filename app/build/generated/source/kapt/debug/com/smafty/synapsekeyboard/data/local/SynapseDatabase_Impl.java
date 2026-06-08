package com.smafty.synapsekeyboard.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.smafty.synapsekeyboard.data.local.dao.ClipboardDao;
import com.smafty.synapsekeyboard.data.local.dao.ClipboardDao_Impl;
import com.smafty.synapsekeyboard.data.local.dao.CustomPromptDao;
import com.smafty.synapsekeyboard.data.local.dao.CustomPromptDao_Impl;
import com.smafty.synapsekeyboard.data.local.dao.MostUsedPromptDao;
import com.smafty.synapsekeyboard.data.local.dao.MostUsedPromptDao_Impl;
import com.smafty.synapsekeyboard.data.local.dao.RecentAiOutputDao;
import com.smafty.synapsekeyboard.data.local.dao.RecentAiOutputDao_Impl;
import com.smafty.synapsekeyboard.data.local.dao.ThemeSettingsDao;
import com.smafty.synapsekeyboard.data.local.dao.ThemeSettingsDao_Impl;
import com.smafty.synapsekeyboard.data.local.dao.UserCreditsDao;
import com.smafty.synapsekeyboard.data.local.dao.UserCreditsDao_Impl;
import com.smafty.synapsekeyboard.data.local.dao.UserWordsDao;
import com.smafty.synapsekeyboard.data.local.dao.UserWordsDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class SynapseDatabase_Impl extends SynapseDatabase {
  private volatile ClipboardDao _clipboardDao;

  private volatile CustomPromptDao _customPromptDao;

  private volatile MostUsedPromptDao _mostUsedPromptDao;

  private volatile UserCreditsDao _userCreditsDao;

  private volatile UserWordsDao _userWordsDao;

  private volatile RecentAiOutputDao _recentAiOutputDao;

  private volatile ThemeSettingsDao _themeSettingsDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(4) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `clipboard_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `content` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `is_pinned` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_clipboard_history_timestamp` ON `clipboard_history` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `custom_prompts` (`localId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `supabaseId` TEXT NOT NULL, `title` TEXT NOT NULL, `prompt` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `most_used_prompts` (`promptId` TEXT NOT NULL, `title` TEXT NOT NULL, `promptText` TEXT NOT NULL, `category` TEXT NOT NULL, `use_count` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, PRIMARY KEY(`promptId`))");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_most_used_prompts_use_count_timestamp` ON `most_used_prompts` (`use_count`, `timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_credits` (`userId` TEXT NOT NULL, `credits` INTEGER NOT NULL, `version` INTEGER NOT NULL, PRIMARY KEY(`userId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `user_words` (`userId` TEXT NOT NULL, `wordsAllowed` INTEGER NOT NULL, `wordsUsed` INTEGER NOT NULL, `version` INTEGER NOT NULL, PRIMARY KEY(`userId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `recent_ai_outputs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `outputContent` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recent_ai_outputs_timestamp` ON `recent_ai_outputs` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `theme_settings` (`id` INTEGER NOT NULL, `activeThemeId` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'edd02d6695c500106172b571e26a094c')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `clipboard_history`");
        db.execSQL("DROP TABLE IF EXISTS `custom_prompts`");
        db.execSQL("DROP TABLE IF EXISTS `most_used_prompts`");
        db.execSQL("DROP TABLE IF EXISTS `user_credits`");
        db.execSQL("DROP TABLE IF EXISTS `user_words`");
        db.execSQL("DROP TABLE IF EXISTS `recent_ai_outputs`");
        db.execSQL("DROP TABLE IF EXISTS `theme_settings`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsClipboardHistory = new HashMap<String, TableInfo.Column>(4);
        _columnsClipboardHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsClipboardHistory.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsClipboardHistory.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsClipboardHistory.put("is_pinned", new TableInfo.Column("is_pinned", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysClipboardHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesClipboardHistory = new HashSet<TableInfo.Index>(1);
        _indicesClipboardHistory.add(new TableInfo.Index("index_clipboard_history_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoClipboardHistory = new TableInfo("clipboard_history", _columnsClipboardHistory, _foreignKeysClipboardHistory, _indicesClipboardHistory);
        final TableInfo _existingClipboardHistory = TableInfo.read(db, "clipboard_history");
        if (!_infoClipboardHistory.equals(_existingClipboardHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "clipboard_history(com.smafty.synapsekeyboard.data.local.entity.ClipboardEntity).\n"
                  + " Expected:\n" + _infoClipboardHistory + "\n"
                  + " Found:\n" + _existingClipboardHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsCustomPrompts = new HashMap<String, TableInfo.Column>(6);
        _columnsCustomPrompts.put("localId", new TableInfo.Column("localId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomPrompts.put("supabaseId", new TableInfo.Column("supabaseId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomPrompts.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomPrompts.put("prompt", new TableInfo.Column("prompt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomPrompts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomPrompts.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCustomPrompts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCustomPrompts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCustomPrompts = new TableInfo("custom_prompts", _columnsCustomPrompts, _foreignKeysCustomPrompts, _indicesCustomPrompts);
        final TableInfo _existingCustomPrompts = TableInfo.read(db, "custom_prompts");
        if (!_infoCustomPrompts.equals(_existingCustomPrompts)) {
          return new RoomOpenHelper.ValidationResult(false, "custom_prompts(com.smafty.synapsekeyboard.data.local.entity.CustomPromptEntity).\n"
                  + " Expected:\n" + _infoCustomPrompts + "\n"
                  + " Found:\n" + _existingCustomPrompts);
        }
        final HashMap<String, TableInfo.Column> _columnsMostUsedPrompts = new HashMap<String, TableInfo.Column>(6);
        _columnsMostUsedPrompts.put("promptId", new TableInfo.Column("promptId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMostUsedPrompts.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMostUsedPrompts.put("promptText", new TableInfo.Column("promptText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMostUsedPrompts.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMostUsedPrompts.put("use_count", new TableInfo.Column("use_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMostUsedPrompts.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMostUsedPrompts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMostUsedPrompts = new HashSet<TableInfo.Index>(1);
        _indicesMostUsedPrompts.add(new TableInfo.Index("index_most_used_prompts_use_count_timestamp", false, Arrays.asList("use_count", "timestamp"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoMostUsedPrompts = new TableInfo("most_used_prompts", _columnsMostUsedPrompts, _foreignKeysMostUsedPrompts, _indicesMostUsedPrompts);
        final TableInfo _existingMostUsedPrompts = TableInfo.read(db, "most_used_prompts");
        if (!_infoMostUsedPrompts.equals(_existingMostUsedPrompts)) {
          return new RoomOpenHelper.ValidationResult(false, "most_used_prompts(com.smafty.synapsekeyboard.data.local.entity.MostUsedPromptEntity).\n"
                  + " Expected:\n" + _infoMostUsedPrompts + "\n"
                  + " Found:\n" + _existingMostUsedPrompts);
        }
        final HashMap<String, TableInfo.Column> _columnsUserCredits = new HashMap<String, TableInfo.Column>(3);
        _columnsUserCredits.put("userId", new TableInfo.Column("userId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserCredits.put("credits", new TableInfo.Column("credits", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserCredits.put("version", new TableInfo.Column("version", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserCredits = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserCredits = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserCredits = new TableInfo("user_credits", _columnsUserCredits, _foreignKeysUserCredits, _indicesUserCredits);
        final TableInfo _existingUserCredits = TableInfo.read(db, "user_credits");
        if (!_infoUserCredits.equals(_existingUserCredits)) {
          return new RoomOpenHelper.ValidationResult(false, "user_credits(com.smafty.synapsekeyboard.data.local.entity.UserCreditsEntity).\n"
                  + " Expected:\n" + _infoUserCredits + "\n"
                  + " Found:\n" + _existingUserCredits);
        }
        final HashMap<String, TableInfo.Column> _columnsUserWords = new HashMap<String, TableInfo.Column>(4);
        _columnsUserWords.put("userId", new TableInfo.Column("userId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserWords.put("wordsAllowed", new TableInfo.Column("wordsAllowed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserWords.put("wordsUsed", new TableInfo.Column("wordsUsed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUserWords.put("version", new TableInfo.Column("version", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUserWords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUserWords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUserWords = new TableInfo("user_words", _columnsUserWords, _foreignKeysUserWords, _indicesUserWords);
        final TableInfo _existingUserWords = TableInfo.read(db, "user_words");
        if (!_infoUserWords.equals(_existingUserWords)) {
          return new RoomOpenHelper.ValidationResult(false, "user_words(com.smafty.synapsekeyboard.data.local.entity.UserWordsEntity).\n"
                  + " Expected:\n" + _infoUserWords + "\n"
                  + " Found:\n" + _existingUserWords);
        }
        final HashMap<String, TableInfo.Column> _columnsRecentAiOutputs = new HashMap<String, TableInfo.Column>(3);
        _columnsRecentAiOutputs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentAiOutputs.put("outputContent", new TableInfo.Column("outputContent", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecentAiOutputs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecentAiOutputs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecentAiOutputs = new HashSet<TableInfo.Index>(1);
        _indicesRecentAiOutputs.add(new TableInfo.Index("index_recent_ai_outputs_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoRecentAiOutputs = new TableInfo("recent_ai_outputs", _columnsRecentAiOutputs, _foreignKeysRecentAiOutputs, _indicesRecentAiOutputs);
        final TableInfo _existingRecentAiOutputs = TableInfo.read(db, "recent_ai_outputs");
        if (!_infoRecentAiOutputs.equals(_existingRecentAiOutputs)) {
          return new RoomOpenHelper.ValidationResult(false, "recent_ai_outputs(com.smafty.synapsekeyboard.data.local.entity.RecentAiOutputEntity).\n"
                  + " Expected:\n" + _infoRecentAiOutputs + "\n"
                  + " Found:\n" + _existingRecentAiOutputs);
        }
        final HashMap<String, TableInfo.Column> _columnsThemeSettings = new HashMap<String, TableInfo.Column>(2);
        _columnsThemeSettings.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsThemeSettings.put("activeThemeId", new TableInfo.Column("activeThemeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysThemeSettings = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesThemeSettings = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoThemeSettings = new TableInfo("theme_settings", _columnsThemeSettings, _foreignKeysThemeSettings, _indicesThemeSettings);
        final TableInfo _existingThemeSettings = TableInfo.read(db, "theme_settings");
        if (!_infoThemeSettings.equals(_existingThemeSettings)) {
          return new RoomOpenHelper.ValidationResult(false, "theme_settings(com.smafty.synapsekeyboard.data.local.entity.ThemeSettingsEntity).\n"
                  + " Expected:\n" + _infoThemeSettings + "\n"
                  + " Found:\n" + _existingThemeSettings);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "edd02d6695c500106172b571e26a094c", "eab3bb4cc96482b4a5c4023d6ca2e4c9");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "clipboard_history","custom_prompts","most_used_prompts","user_credits","user_words","recent_ai_outputs","theme_settings");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `clipboard_history`");
      _db.execSQL("DELETE FROM `custom_prompts`");
      _db.execSQL("DELETE FROM `most_used_prompts`");
      _db.execSQL("DELETE FROM `user_credits`");
      _db.execSQL("DELETE FROM `user_words`");
      _db.execSQL("DELETE FROM `recent_ai_outputs`");
      _db.execSQL("DELETE FROM `theme_settings`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ClipboardDao.class, ClipboardDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CustomPromptDao.class, CustomPromptDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(MostUsedPromptDao.class, MostUsedPromptDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserCreditsDao.class, UserCreditsDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserWordsDao.class, UserWordsDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RecentAiOutputDao.class, RecentAiOutputDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ThemeSettingsDao.class, ThemeSettingsDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ClipboardDao clipboardDao() {
    if (_clipboardDao != null) {
      return _clipboardDao;
    } else {
      synchronized(this) {
        if(_clipboardDao == null) {
          _clipboardDao = new ClipboardDao_Impl(this);
        }
        return _clipboardDao;
      }
    }
  }

  @Override
  public CustomPromptDao customPromptDao() {
    if (_customPromptDao != null) {
      return _customPromptDao;
    } else {
      synchronized(this) {
        if(_customPromptDao == null) {
          _customPromptDao = new CustomPromptDao_Impl(this);
        }
        return _customPromptDao;
      }
    }
  }

  @Override
  public MostUsedPromptDao mostUsedPromptDao() {
    if (_mostUsedPromptDao != null) {
      return _mostUsedPromptDao;
    } else {
      synchronized(this) {
        if(_mostUsedPromptDao == null) {
          _mostUsedPromptDao = new MostUsedPromptDao_Impl(this);
        }
        return _mostUsedPromptDao;
      }
    }
  }

  @Override
  public UserCreditsDao userCreditsDao() {
    if (_userCreditsDao != null) {
      return _userCreditsDao;
    } else {
      synchronized(this) {
        if(_userCreditsDao == null) {
          _userCreditsDao = new UserCreditsDao_Impl(this);
        }
        return _userCreditsDao;
      }
    }
  }

  @Override
  public UserWordsDao userWordsDao() {
    if (_userWordsDao != null) {
      return _userWordsDao;
    } else {
      synchronized(this) {
        if(_userWordsDao == null) {
          _userWordsDao = new UserWordsDao_Impl(this);
        }
        return _userWordsDao;
      }
    }
  }

  @Override
  public RecentAiOutputDao recentAiOutputDao() {
    if (_recentAiOutputDao != null) {
      return _recentAiOutputDao;
    } else {
      synchronized(this) {
        if(_recentAiOutputDao == null) {
          _recentAiOutputDao = new RecentAiOutputDao_Impl(this);
        }
        return _recentAiOutputDao;
      }
    }
  }

  @Override
  public ThemeSettingsDao themeSettingsDao() {
    if (_themeSettingsDao != null) {
      return _themeSettingsDao;
    } else {
      synchronized(this) {
        if(_themeSettingsDao == null) {
          _themeSettingsDao = new ThemeSettingsDao_Impl(this);
        }
        return _themeSettingsDao;
      }
    }
  }
}
