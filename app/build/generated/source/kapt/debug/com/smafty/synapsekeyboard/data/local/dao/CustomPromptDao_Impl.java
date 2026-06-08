package com.smafty.synapsekeyboard.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.smafty.synapsekeyboard.data.local.entity.CustomPromptEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class CustomPromptDao_Impl implements CustomPromptDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CustomPromptEntity> __insertionAdapterOfCustomPromptEntity;

  private final EntityDeletionOrUpdateAdapter<CustomPromptEntity> __deletionAdapterOfCustomPromptEntity;

  private final EntityDeletionOrUpdateAdapter<CustomPromptEntity> __updateAdapterOfCustomPromptEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public CustomPromptDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCustomPromptEntity = new EntityInsertionAdapter<CustomPromptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `custom_prompts` (`localId`,`supabaseId`,`title`,`prompt`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CustomPromptEntity entity) {
        statement.bindLong(1, entity.getLocalId());
        if (entity.getSupabaseId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getSupabaseId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTitle());
        }
        if (entity.getPrompt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPrompt());
        }
        statement.bindLong(5, entity.getCreatedAt());
        statement.bindLong(6, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfCustomPromptEntity = new EntityDeletionOrUpdateAdapter<CustomPromptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `custom_prompts` WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CustomPromptEntity entity) {
        statement.bindLong(1, entity.getLocalId());
      }
    };
    this.__updateAdapterOfCustomPromptEntity = new EntityDeletionOrUpdateAdapter<CustomPromptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `custom_prompts` SET `localId` = ?,`supabaseId` = ?,`title` = ?,`prompt` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `localId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CustomPromptEntity entity) {
        statement.bindLong(1, entity.getLocalId());
        if (entity.getSupabaseId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getSupabaseId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTitle());
        }
        if (entity.getPrompt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPrompt());
        }
        statement.bindLong(5, entity.getCreatedAt());
        statement.bindLong(6, entity.getUpdatedAt());
        statement.bindLong(7, entity.getLocalId());
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM custom_prompts";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final CustomPromptEntity prompt,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCustomPromptEntity.insertAndReturnId(prompt);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final CustomPromptEntity prompt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCustomPromptEntity.handle(prompt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final CustomPromptEntity prompt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCustomPromptEntity.handle(prompt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CustomPromptEntity>> getAllFlow() {
    final String _sql = "SELECT * FROM custom_prompts ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"custom_prompts"}, new Callable<List<CustomPromptEntity>>() {
      @Override
      @NonNull
      public List<CustomPromptEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfSupabaseId = CursorUtil.getColumnIndexOrThrow(_cursor, "supabaseId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<CustomPromptEntity> _result = new ArrayList<CustomPromptEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CustomPromptEntity _item;
            final int _tmpLocalId;
            _tmpLocalId = _cursor.getInt(_cursorIndexOfLocalId);
            final String _tmpSupabaseId;
            if (_cursor.isNull(_cursorIndexOfSupabaseId)) {
              _tmpSupabaseId = null;
            } else {
              _tmpSupabaseId = _cursor.getString(_cursorIndexOfSupabaseId);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpPrompt;
            if (_cursor.isNull(_cursorIndexOfPrompt)) {
              _tmpPrompt = null;
            } else {
              _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new CustomPromptEntity(_tmpLocalId,_tmpSupabaseId,_tmpTitle,_tmpPrompt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAll(final Continuation<? super List<CustomPromptEntity>> $completion) {
    final String _sql = "SELECT * FROM custom_prompts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<CustomPromptEntity>>() {
      @Override
      @NonNull
      public List<CustomPromptEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfSupabaseId = CursorUtil.getColumnIndexOrThrow(_cursor, "supabaseId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<CustomPromptEntity> _result = new ArrayList<CustomPromptEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CustomPromptEntity _item;
            final int _tmpLocalId;
            _tmpLocalId = _cursor.getInt(_cursorIndexOfLocalId);
            final String _tmpSupabaseId;
            if (_cursor.isNull(_cursorIndexOfSupabaseId)) {
              _tmpSupabaseId = null;
            } else {
              _tmpSupabaseId = _cursor.getString(_cursorIndexOfSupabaseId);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpPrompt;
            if (_cursor.isNull(_cursorIndexOfPrompt)) {
              _tmpPrompt = null;
            } else {
              _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new CustomPromptEntity(_tmpLocalId,_tmpSupabaseId,_tmpTitle,_tmpPrompt,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final int id, final Continuation<? super CustomPromptEntity> $completion) {
    final String _sql = "SELECT * FROM custom_prompts WHERE localId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CustomPromptEntity>() {
      @Override
      @Nullable
      public CustomPromptEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfSupabaseId = CursorUtil.getColumnIndexOrThrow(_cursor, "supabaseId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPrompt = CursorUtil.getColumnIndexOrThrow(_cursor, "prompt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final CustomPromptEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpLocalId;
            _tmpLocalId = _cursor.getInt(_cursorIndexOfLocalId);
            final String _tmpSupabaseId;
            if (_cursor.isNull(_cursorIndexOfSupabaseId)) {
              _tmpSupabaseId = null;
            } else {
              _tmpSupabaseId = _cursor.getString(_cursorIndexOfSupabaseId);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpPrompt;
            if (_cursor.isNull(_cursorIndexOfPrompt)) {
              _tmpPrompt = null;
            } else {
              _tmpPrompt = _cursor.getString(_cursorIndexOfPrompt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new CustomPromptEntity(_tmpLocalId,_tmpSupabaseId,_tmpTitle,_tmpPrompt,_tmpCreatedAt,_tmpUpdatedAt);
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

  @Override
  public Object replaceAll(final List<CustomPromptEntity> prompts,
      final Continuation<? super Unit> $completion) {
    return CustomPromptDao.DefaultImpls.replaceAll(CustomPromptDao_Impl.this, prompts, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
