package com.smafty.synapsekeyboard.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.smafty.synapsekeyboard.data.local.entity.MostUsedPromptEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class MostUsedPromptDao_Impl implements MostUsedPromptDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MostUsedPromptEntity> __insertionAdapterOfMostUsedPromptEntity;

  private final EntityDeletionOrUpdateAdapter<MostUsedPromptEntity> __updateAdapterOfMostUsedPromptEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfPruneBelowTop20;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public MostUsedPromptDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMostUsedPromptEntity = new EntityInsertionAdapter<MostUsedPromptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `most_used_prompts` (`promptId`,`title`,`promptText`,`category`,`use_count`,`timestamp`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MostUsedPromptEntity entity) {
        if (entity.getPromptId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getPromptId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getPromptText() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getPromptText());
        }
        if (entity.getCategory() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCategory());
        }
        statement.bindLong(5, entity.getUseCount());
        statement.bindLong(6, entity.getTimestamp());
      }
    };
    this.__updateAdapterOfMostUsedPromptEntity = new EntityDeletionOrUpdateAdapter<MostUsedPromptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `most_used_prompts` SET `promptId` = ?,`title` = ?,`promptText` = ?,`category` = ?,`use_count` = ?,`timestamp` = ? WHERE `promptId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MostUsedPromptEntity entity) {
        if (entity.getPromptId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getPromptId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getTitle());
        }
        if (entity.getPromptText() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getPromptText());
        }
        if (entity.getCategory() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getCategory());
        }
        statement.bindLong(5, entity.getUseCount());
        statement.bindLong(6, entity.getTimestamp());
        if (entity.getPromptId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getPromptId());
        }
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM most_used_prompts WHERE promptId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfPruneBelowTop20 = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM most_used_prompts WHERE promptId NOT IN (SELECT promptId FROM most_used_prompts ORDER BY use_count DESC, timestamp DESC LIMIT 20)";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM most_used_prompts";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final MostUsedPromptEntity prompt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMostUsedPromptEntity.insert(prompt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final MostUsedPromptEntity prompt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfMostUsedPromptEntity.handle(prompt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementPromptUsage(final String promptId, final String title,
      final String promptText, final String category,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> MostUsedPromptDao.DefaultImpls.incrementPromptUsage(MostUsedPromptDao_Impl.this, promptId, title, promptText, category, __cont), $completion);
  }

  @Override
  public Object deleteById(final String promptId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        if (promptId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, promptId);
        }
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object pruneBelowTop20(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfPruneBelowTop20.acquire();
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
          __preparedStmtOfPruneBelowTop20.release(_stmt);
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
  public Flow<List<MostUsedPromptEntity>> getTop20PromptsFlow() {
    final String _sql = "SELECT * FROM most_used_prompts ORDER BY use_count DESC, timestamp DESC LIMIT 20";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"most_used_prompts"}, new Callable<List<MostUsedPromptEntity>>() {
      @Override
      @NonNull
      public List<MostUsedPromptEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPromptId = CursorUtil.getColumnIndexOrThrow(_cursor, "promptId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPromptText = CursorUtil.getColumnIndexOrThrow(_cursor, "promptText");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUseCount = CursorUtil.getColumnIndexOrThrow(_cursor, "use_count");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<MostUsedPromptEntity> _result = new ArrayList<MostUsedPromptEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MostUsedPromptEntity _item;
            final String _tmpPromptId;
            if (_cursor.isNull(_cursorIndexOfPromptId)) {
              _tmpPromptId = null;
            } else {
              _tmpPromptId = _cursor.getString(_cursorIndexOfPromptId);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpPromptText;
            if (_cursor.isNull(_cursorIndexOfPromptText)) {
              _tmpPromptText = null;
            } else {
              _tmpPromptText = _cursor.getString(_cursorIndexOfPromptText);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final int _tmpUseCount;
            _tmpUseCount = _cursor.getInt(_cursorIndexOfUseCount);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new MostUsedPromptEntity(_tmpPromptId,_tmpTitle,_tmpPromptText,_tmpCategory,_tmpUseCount,_tmpTimestamp);
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
  public Object getById(final String promptId,
      final Continuation<? super MostUsedPromptEntity> $completion) {
    final String _sql = "SELECT * FROM most_used_prompts WHERE promptId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (promptId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, promptId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MostUsedPromptEntity>() {
      @Override
      @Nullable
      public MostUsedPromptEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPromptId = CursorUtil.getColumnIndexOrThrow(_cursor, "promptId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPromptText = CursorUtil.getColumnIndexOrThrow(_cursor, "promptText");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfUseCount = CursorUtil.getColumnIndexOrThrow(_cursor, "use_count");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final MostUsedPromptEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPromptId;
            if (_cursor.isNull(_cursorIndexOfPromptId)) {
              _tmpPromptId = null;
            } else {
              _tmpPromptId = _cursor.getString(_cursorIndexOfPromptId);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpPromptText;
            if (_cursor.isNull(_cursorIndexOfPromptText)) {
              _tmpPromptText = null;
            } else {
              _tmpPromptText = _cursor.getString(_cursorIndexOfPromptText);
            }
            final String _tmpCategory;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmpCategory = null;
            } else {
              _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            }
            final int _tmpUseCount;
            _tmpUseCount = _cursor.getInt(_cursorIndexOfUseCount);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result = new MostUsedPromptEntity(_tmpPromptId,_tmpTitle,_tmpPromptText,_tmpCategory,_tmpUseCount,_tmpTimestamp);
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
