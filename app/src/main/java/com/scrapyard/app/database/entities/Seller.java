package com.scrapyard.app.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "sellers")
public class Seller {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    @NonNull
    private String nationalId;

    private double totalWeight;
    private double totalAmount;
    private int transactionCount;

    // Stores the absolute file path to the seller's photo on device storage.
    // Null means no photo has been set yet.
    private String photoPath;

    public Seller(@NonNull String name, @NonNull String nationalId) {
        this.name = name;
        this.nationalId = nationalId;
        this.totalWeight = 0;
        this.totalAmount = 0;
        this.transactionCount = 0;
        this.photoPath = null;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name; }

    @NonNull
    public String getNationalId() { return nationalId; }
    public void setNationalId(@NonNull String nationalId) { this.nationalId = nationalId; }

    public double getTotalWeight() { return totalWeight; }
    public void setTotalWeight(double totalWeight) { this.totalWeight = totalWeight; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public int getTransactionCount() { return transactionCount; }
    public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }

    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }

    public boolean hasPhoto() { return photoPath != null && !photoPath.isEmpty(); }
}
