package com.example.aisearchlab.models;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LeaderboardDao_Impl implements LeaderboardDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ScoreEntity> __insertionAdapterOfScoreEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllScores;

  public LeaderboardDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfScoreEntity = new EntityInsertionAdapter<ScoreEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `leaderboard_scores` (`id`,`gameName`,`algorithmName`,`nodesExplored`,`pathCost`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final ScoreEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.gameName == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.gameName);
        }
        if (entity.algorithmName == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.algorithmName);
        }
        statement.bindLong(4, entity.nodesExplored);
        statement.bindDouble(5, entity.pathCost);
        statement.bindLong(6, entity.timestamp);
      }
    };
    this.__preparedStmtOfDeleteAllScores = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM leaderboard_scores";
        return _query;
      }
    };
  }

  @Override
  public void insertScore(final ScoreEntity score) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfScoreEntity.insert(score);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteAllScores() {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllScores.acquire();
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteAllScores.release(_stmt);
    }
  }

  @Override
  public LiveData<List<ScoreEntity>> getTopScoresForGame(final String gameName) {
    final String _sql = "SELECT * FROM leaderboard_scores WHERE gameName = ? ORDER BY pathCost ASC, nodesExplored ASC LIMIT 10";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (gameName == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, gameName);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"leaderboard_scores"}, false, new Callable<List<ScoreEntity>>() {
      @Override
      @Nullable
      public List<ScoreEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameName = CursorUtil.getColumnIndexOrThrow(_cursor, "gameName");
          final int _cursorIndexOfAlgorithmName = CursorUtil.getColumnIndexOrThrow(_cursor, "algorithmName");
          final int _cursorIndexOfNodesExplored = CursorUtil.getColumnIndexOrThrow(_cursor, "nodesExplored");
          final int _cursorIndexOfPathCost = CursorUtil.getColumnIndexOrThrow(_cursor, "pathCost");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ScoreEntity> _result = new ArrayList<ScoreEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScoreEntity _item;
            final String _tmpGameName;
            if (_cursor.isNull(_cursorIndexOfGameName)) {
              _tmpGameName = null;
            } else {
              _tmpGameName = _cursor.getString(_cursorIndexOfGameName);
            }
            final String _tmpAlgorithmName;
            if (_cursor.isNull(_cursorIndexOfAlgorithmName)) {
              _tmpAlgorithmName = null;
            } else {
              _tmpAlgorithmName = _cursor.getString(_cursorIndexOfAlgorithmName);
            }
            final int _tmpNodesExplored;
            _tmpNodesExplored = _cursor.getInt(_cursorIndexOfNodesExplored);
            final double _tmpPathCost;
            _tmpPathCost = _cursor.getDouble(_cursorIndexOfPathCost);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ScoreEntity(_tmpGameName,_tmpAlgorithmName,_tmpNodesExplored,_tmpPathCost,_tmpTimestamp);
            _item.id = _cursor.getInt(_cursorIndexOfId);
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
  public LiveData<List<ScoreEntity>> getRecentScores() {
    final String _sql = "SELECT * FROM leaderboard_scores ORDER BY timestamp DESC LIMIT 50";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"leaderboard_scores"}, false, new Callable<List<ScoreEntity>>() {
      @Override
      @Nullable
      public List<ScoreEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfGameName = CursorUtil.getColumnIndexOrThrow(_cursor, "gameName");
          final int _cursorIndexOfAlgorithmName = CursorUtil.getColumnIndexOrThrow(_cursor, "algorithmName");
          final int _cursorIndexOfNodesExplored = CursorUtil.getColumnIndexOrThrow(_cursor, "nodesExplored");
          final int _cursorIndexOfPathCost = CursorUtil.getColumnIndexOrThrow(_cursor, "pathCost");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ScoreEntity> _result = new ArrayList<ScoreEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScoreEntity _item;
            final String _tmpGameName;
            if (_cursor.isNull(_cursorIndexOfGameName)) {
              _tmpGameName = null;
            } else {
              _tmpGameName = _cursor.getString(_cursorIndexOfGameName);
            }
            final String _tmpAlgorithmName;
            if (_cursor.isNull(_cursorIndexOfAlgorithmName)) {
              _tmpAlgorithmName = null;
            } else {
              _tmpAlgorithmName = _cursor.getString(_cursorIndexOfAlgorithmName);
            }
            final int _tmpNodesExplored;
            _tmpNodesExplored = _cursor.getInt(_cursorIndexOfNodesExplored);
            final double _tmpPathCost;
            _tmpPathCost = _cursor.getDouble(_cursorIndexOfPathCost);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ScoreEntity(_tmpGameName,_tmpAlgorithmName,_tmpNodesExplored,_tmpPathCost,_tmpTimestamp);
            _item.id = _cursor.getInt(_cursorIndexOfId);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
