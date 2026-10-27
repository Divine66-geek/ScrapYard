package com.scrapyard.app.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(
    tableName = "transactions",
    foreignKeys = @ForeignKey(
        entity = Seller.class,
        parentColumns = "id",
        childColumns = "sellerId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("sellerId")}
)
public class Transaction {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int sellerId;
    private double weight;
    private double amount;

    @NonNull
    private String date;

    @NonNull
    private String notes;

    public Transaction(int sellerId, double weight, double amount, @NonNull String date, @NonNull String notes) {
        this.sellerId = sellerId;
        this.weight = weight;
        this.amount = amount;
        this.date = date;
        this.notes = notes;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    @NonNull
    public String getDate() { return date; }
    public void setDate(@NonNull String date) { this.date = date; }

    @NonNull
    public String getNotes() { return notes; }
    public void setNotes(@NonNull String notes) { this.notes = notes; }
}
