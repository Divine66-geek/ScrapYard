package com.scrapyard.app.database.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.scrapyard.app.database.entities.Seller;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class SellerDao_Impl implements SellerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Seller> __insertionAdapterOfSeller;

  private final EntityDeletionOrUpdateAdapter<Seller> __deletionAdapterOfSeller;

  private final EntityDeletionOrUpdateAdapter<Seller> __updateAdapterOfSeller;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSellerTotals;

  private final SharedSQLiteStatement __preparedStmtOfReverseSellerTotals;

  public SellerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSeller = new EntityInsertionAdapter<Seller>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `sellers` (`id`,`name`,`nationalId`,`totalWeight`,`totalAmount`,`transactionCount`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Seller entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getNationalId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getNationalId());
        }
        statement.bindDouble(4, entity.getTotalWeight());
        statement.bindDouble(5, entity.getTotalAmount());
        statement.bindLong(6, entity.getTransactionCount());
      }
    };
    this.__deletionAdapterOfSeller = new EntityDeletionOrUpdateAdapter<Seller>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `sellers` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Seller entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfSeller = new EntityDeletionOrUpdateAdapter<Seller>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sellers` SET `id` = ?,`name` = ?,`nationalId` = ?,`totalWeight` = ?,`totalAmount` = ?,`transactionCount` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Seller entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        if (entity.getNationalId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getNationalId());
        }
        statement.bindDouble(4, entity.getTotalWeight());
        statement.bindDouble(5, entity.getTotalAmount());
        statement.bindLong(6, entity.getTransactionCount());
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateSellerTotals = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sellers SET totalWeight = totalWeight + ?, totalAmount = totalAmount + ?, transactionCount = transactionCount + 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfReverseSellerTotals = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE sellers SET totalWeight = totalWeight - ?, totalAmount = totalAmount - ?, transactionCount = transactionCount - 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final Seller seller) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfSeller.insertAndReturnId(seller);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Seller seller) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfSeller.handle(seller);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Seller seller) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfSeller.handle(seller);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void updateSellerTotals(final int sellerId, final double weight, final double amount) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSellerTotals.acquire();
    int _argIndex = 1;
    _stmt.bindDouble(_argIndex, weight);
    _argIndex = 2;
    _stmt.bindDouble(_argIndex, amount);
    _argIndex = 3;
    _stmt.bindLong(_argIndex, sellerId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfUpdateSellerTotals.release(_stmt);
    }
  }

  @Override
  public void reverseSellerTotals(final int sellerId, final double weight, final double amount) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfReverseSellerTotals.acquire();
    int _argIndex = 1;
    _stmt.bindDouble(_argIndex, weight);
    _argIndex = 2;
    _stmt.bindDouble(_argIndex, amount);
    _argIndex = 3;
    _stmt.bindLong(_argIndex, sellerId);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfReverseSellerTotals.release(_stmt);
    }
  }

  @Override
  public LiveData<List<Seller>> getAllSellers() {
    final String _sql = "SELECT * FROM sellers ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"sellers"}, false, new Callable<List<Seller>>() {
      @Override
      @Nullable
      public List<Seller> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfNationalId = CursorUtil.getColumnIndexOrThrow(_cursor, "nationalId");
          final int _cursorIndexOfTotalWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "totalWeight");
          final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
          final int _cursorIndexOfTransactionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionCount");
          final List<Seller> _result = new ArrayList<Seller>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Seller _item;
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpNationalId;
            if (_cursor.isNull(_cursorIndexOfNationalId)) {
              _tmpNationalId = null;
            } else {
              _tmpNationalId = _cursor.getString(_cursorIndexOfNationalId);
            }
            _item = new Seller(_tmpName,_tmpNationalId);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final double _tmpTotalWeight;
            _tmpTotalWeight = _cursor.getDouble(_cursorIndexOfTotalWeight);
            _item.setTotalWeight(_tmpTotalWeight);
            final double _tmpTotalAmount;
            _tmpTotalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
            _item.setTotalAmount(_tmpTotalAmount);
            final int _tmpTransactionCount;
            _tmpTransactionCount = _cursor.getInt(_cursorIndexOfTransactionCount);
            _item.setTransactionCount(_tmpTransactionCount);
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
  public List<Seller> getAllSellersList() {
    final String _sql = "SELECT * FROM sellers ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfNationalId = CursorUtil.getColumnIndexOrThrow(_cursor, "nationalId");
      final int _cursorIndexOfTotalWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "totalWeight");
      final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
      final int _cursorIndexOfTransactionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionCount");
      final List<Seller> _result = new ArrayList<Seller>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Seller _item;
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        final String _tmpNationalId;
        if (_cursor.isNull(_cursorIndexOfNationalId)) {
          _tmpNationalId = null;
        } else {
          _tmpNationalId = _cursor.getString(_cursorIndexOfNationalId);
        }
        _item = new Seller(_tmpName,_tmpNationalId);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        final double _tmpTotalWeight;
        _tmpTotalWeight = _cursor.getDouble(_cursorIndexOfTotalWeight);
        _item.setTotalWeight(_tmpTotalWeight);
        final double _tmpTotalAmount;
        _tmpTotalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
        _item.setTotalAmount(_tmpTotalAmount);
        final int _tmpTransactionCount;
        _tmpTransactionCount = _cursor.getInt(_cursorIndexOfTransactionCount);
        _item.setTransactionCount(_tmpTransactionCount);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Seller getSellerById(final int id) {
    final String _sql = "SELECT * FROM sellers WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfNationalId = CursorUtil.getColumnIndexOrThrow(_cursor, "nationalId");
      final int _cursorIndexOfTotalWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "totalWeight");
      final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
      final int _cursorIndexOfTransactionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionCount");
      final Seller _result;
      if (_cursor.moveToFirst()) {
        final String _tmpName;
        if (_cursor.isNull(_cursorIndexOfName)) {
          _tmpName = null;
        } else {
          _tmpName = _cursor.getString(_cursorIndexOfName);
        }
        final String _tmpNationalId;
        if (_cursor.isNull(_cursorIndexOfNationalId)) {
          _tmpNationalId = null;
        } else {
          _tmpNationalId = _cursor.getString(_cursorIndexOfNationalId);
        }
        _result = new Seller(_tmpName,_tmpNationalId);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _result.setId(_tmpId);
        final double _tmpTotalWeight;
        _tmpTotalWeight = _cursor.getDouble(_cursorIndexOfTotalWeight);
        _result.setTotalWeight(_tmpTotalWeight);
        final double _tmpTotalAmount;
        _tmpTotalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
        _result.setTotalAmount(_tmpTotalAmount);
        final int _tmpTransactionCount;
        _tmpTransactionCount = _cursor.getInt(_cursorIndexOfTransactionCount);
        _result.setTransactionCount(_tmpTransactionCount);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public LiveData<List<Seller>> searchSellers(final String query) {
    final String _sql = "SELECT * FROM sellers WHERE name LIKE '%' || ? || '%' OR nationalId LIKE '%' || ? || '%' ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    _argIndex = 2;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    return __db.getInvalidationTracker().createLiveData(new String[] {"sellers"}, false, new Callable<List<Seller>>() {
      @Override
      @Nullable
      public List<Seller> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfNationalId = CursorUtil.getColumnIndexOrThrow(_cursor, "nationalId");
          final int _cursorIndexOfTotalWeight = CursorUtil.getColumnIndexOrThrow(_cursor, "totalWeight");
          final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
          final int _cursorIndexOfTransactionCount = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionCount");
          final List<Seller> _result = new ArrayList<Seller>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Seller _item;
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpNationalId;
            if (_cursor.isNull(_cursorIndexOfNationalId)) {
              _tmpNationalId = null;
            } else {
              _tmpNationalId = _cursor.getString(_cursorIndexOfNationalId);
            }
            _item = new Seller(_tmpName,_tmpNationalId);
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            _item.setId(_tmpId);
            final double _tmpTotalWeight;
            _tmpTotalWeight = _cursor.getDouble(_cursorIndexOfTotalWeight);
            _item.setTotalWeight(_tmpTotalWeight);
            final double _tmpTotalAmount;
            _tmpTotalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
            _item.setTotalAmount(_tmpTotalAmount);
            final int _tmpTransactionCount;
            _tmpTransactionCount = _cursor.getInt(_cursorIndexOfTransactionCount);
            _item.setTransactionCount(_tmpTransactionCount);
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
  public int getSellersCount() {
    final String _sql = "SELECT COUNT(*) FROM sellers";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getInt(0);
      } else {
        _result = 0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public double getTotalWeight() {
    final String _sql = "SELECT SUM(totalWeight) FROM sellers";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final double _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getDouble(0);
      } else {
        _result = 0.0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public double getTotalAmount() {
    final String _sql = "SELECT SUM(totalAmount) FROM sellers";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final double _result;
      if (_cursor.moveToFirst()) {
        _result = _cursor.getDouble(0);
      } else {
        _result = 0.0;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
