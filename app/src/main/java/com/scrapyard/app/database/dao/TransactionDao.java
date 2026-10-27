package com.scrapyard.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.scrapyard.app.database.entities.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("SELECT * FROM transactions WHERE sellerId = :sellerId ORDER BY date DESC")
    LiveData<List<Transaction>> getTransactionsBySeller(int sellerId);

    @Query("SELECT * FROM transactions WHERE sellerId = :sellerId ORDER BY date DESC")
    List<Transaction> getTransactionsBySellerList(int sellerId);

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<Transaction>> getAllTransactions();

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<Transaction> getAllTransactionsList();

    @Query("SELECT COUNT(*) FROM transactions")
    int getTransactionsCount();

    @Query("SELECT SUM(weight) FROM transactions")
    double getTotalWeight();

    @Query("SELECT SUM(amount) FROM transactions")
    double getTotalAmount();

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<Transaction> getTransactionsByDateRange(String startDate, String endDate);

    @Query("SELECT * FROM transactions WHERE id = :id")
    Transaction getTransactionById(int id);
}
