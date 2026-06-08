package com.smafty.synapsekeyboard.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.smafty.synapsekeyboard.data.local.entity.UserWordsEntity;
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
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class UserWordsDao_Impl implements UserWordsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserWordsEntity> __insertionAdapterOfUserWordsEntity;

  private final SharedSQLiteStatement __preparedStmtOfIncrementWordsUsed;

  public UserWordsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserWordsEntity = new EntityInsertionAdapter<UserWordsEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_words` (`userId`,`wordsAllowed`,`wordsUsed`,`version`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserWordsEntity entity) {
        if (entity.getUserId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getUserId());
        }
        statement.bindLong(2, entity.getWordsAllowed());
        statement.bindLong(3, entity.getWordsUsed());
        statement.bindLong(4, entity.getVersion());
      }
    };
    this.__preparedStmtOfIncrementWordsUsed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE user_words SET wordsUsed = wordsUsed + ? WHERE userId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object saveUserWords(final UserWordsEntity wordsEntity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserWordsEntity.insert(wordsEntity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementWordsUsed(final String userId, final int wordsUsedDelta,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementWordsUsed.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, wordsUsedDelta);
        _argIndex = 2;
        if (userId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, userId);
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
          __preparedStmtOfIncrementWordsUsed.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getUserWords(final String userId,
      final Continuation<? super UserWordsEntity> $completion) {
    final String _sql = "SELECT * FROM user_words WHERE userId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserWordsEntity>() {
      @Override
      @Nullable
      public UserWordsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfWordsAllowed = CursorUtil.getColumnIndexOrThrow(_cursor, "wordsAllowed");
          final int _cursorIndexOfWordsUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "wordsUsed");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final UserWordsEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final int _tmpWordsAllowed;
            _tmpWordsAllowed = _cursor.getInt(_cursorIndexOfWordsAllowed);
            final int _tmpWordsUsed;
            _tmpWordsUsed = _cursor.getInt(_cursorIndexOfWordsUsed);
            final int _tmpVersion;
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion);
            _result = new UserWordsEntity(_tmpUserId,_tmpWordsAllowed,_tmpWordsUsed,_tmpVersion);
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
  public Flow<UserWordsEntity> getUserWordsFlow(final String userId) {
    final String _sql = "SELECT * FROM user_words WHERE userId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_words"}, new Callable<UserWordsEntity>() {
      @Override
      @Nullable
      public UserWordsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfWordsAllowed = CursorUtil.getColumnIndexOrThrow(_cursor, "wordsAllowed");
          final int _cursorIndexOfWordsUsed = CursorUtil.getColumnIndexOrThrow(_cursor, "wordsUsed");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final UserWordsEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final int _tmpWordsAllowed;
            _tmpWordsAllowed = _cursor.getInt(_cursorIndexOfWordsAllowed);
            final int _tmpWordsUsed;
            _tmpWordsUsed = _cursor.getInt(_cursorIndexOfWordsUsed);
            final int _tmpVersion;
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion);
            _result = new UserWordsEntity(_tmpUserId,_tmpWordsAllowed,_tmpWordsUsed,_tmpVersion);
          } else {
            _result = null;
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
