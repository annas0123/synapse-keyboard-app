package com.smafty.synapsekeyboard.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.smafty.synapsekeyboard.data.local.entity.ThemeSettingsEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@SuppressWarnings({"unchecked", "deprecation"})
public final class ThemeSettingsDao_Impl implements ThemeSettingsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ThemeSettingsEntity> __insertionAdapterOfThemeSettingsEntity;

  public ThemeSettingsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfThemeSettingsEntity = new EntityInsertionAdapter<ThemeSettingsEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `theme_settings` (`id`,`activeThemeId`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ThemeSettingsEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getActiveThemeId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getActiveThemeId());
        }
      }
    };
  }

  @Override
  public Object saveThemeSettings(final ThemeSettingsEntity settings,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfThemeSettingsEntity.insert(settings);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getThemeSettings(final Continuation<? super ThemeSettingsEntity> $completion) {
    final String _sql = "SELECT * FROM theme_settings WHERE id = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ThemeSettingsEntity>() {
      @Override
      @Nullable
      public ThemeSettingsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfActiveThemeId = CursorUtil.getColumnIndexOrThrow(_cursor, "activeThemeId");
          final ThemeSettingsEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpActiveThemeId;
            if (_cursor.isNull(_cursorIndexOfActiveThemeId)) {
              _tmpActiveThemeId = null;
            } else {
              _tmpActiveThemeId = _cursor.getString(_cursorIndexOfActiveThemeId);
            }
            _result = new ThemeSettingsEntity(_tmpId,_tmpActiveThemeId);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
