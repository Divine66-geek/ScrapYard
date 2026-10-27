package com.scrapyard.app.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.scrapyard.app.database.entities.Seller;

import java.util.List;

@Dao
public interface SellerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Seller seller);

    @Update
    void update(Seller seller);

    @Delete
    void delete(Seller seller);

    @Query("SELECT * FROM sellers ORDER BY name ASC")
    LiveData<List<Seller>> getAllSellers();

    @Query("SELECT * FROM sellers ORDER BY name ASC")
    List<Seller> getAllSellersList();

    @Query("SELECT * FROM sellers WHERE id = :id")
    Seller getSellerById(int id);

    @Query("SELECT * FROM sellers WHERE name LIKE '%' || :query || '%' OR nationalId LIKE '%' || :query || '%' ORDER BY name ASC")
    LiveData<List<Seller>> searchSellers(String query);

    @Query("SELECT COUNT(*) FROM sellers")
    int getSellersCount();

    @Query("SELECT SUM(totalWeight) FROM sellers")
    double getTotalWeight();

    @Query("SELECT SUM(totalAmount) FROM sellers")
    double getTotalAmount();

    @Query("UPDATE sellers SET totalWeight = totalWeight + :weight, totalAmount = totalAmount + :amount, transactionCount = transactionCount + 1 WHERE id = :sellerId")
    void updateSellerTotals(int sellerId, double weight, double amount);

    @Query("UPDATE sellers SET totalWeight = totalWeight - :weight, totalAmount = totalAmount - :amount, transactionCount = transactionCount - 1 WHERE id = :sellerId")
    void reverseSellerTotals(int sellerId, double weight, double amount);

    // Updates only the photo path for a given seller
    @Query("UPDATE sellers SET photoPath = :photoPath WHERE id = :sellerId")
    void updatePhoto(int sellerId, String photoPath);
}
