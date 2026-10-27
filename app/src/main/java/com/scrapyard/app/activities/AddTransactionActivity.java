package com.scrapyard.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.scrapyard.app.R;
import com.scrapyard.app.database.AppDatabase;
import com.scrapyard.app.database.entities.Transaction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText etWeight, etAmount, etNotes;
    private TextView tvSellerName;
    private AppDatabase database;
    private int sellerId;
    private String sellerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        database = AppDatabase.getInstance(this);

        sellerId = getIntent().getIntExtra("SELLER_ID", -1);
        sellerName = getIntent().getStringExtra("SELLER_NAME");

        if (sellerId == -1) {
            Toast.makeText(this, "Invalid seller", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("New Transaction");
        }

        tvSellerName = findViewById(R.id.tvSellerName);
        etWeight = findViewById(R.id.etWeight);
        etAmount = findViewById(R.id.etAmount);
        etNotes = findViewById(R.id.etNotes);
        Button btnSave = findViewById(R.id.btnSave);

        tvSellerName.setText(sellerName != null ? sellerName : "Unknown Seller");

        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        String weightStr = etWeight.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (TextUtils.isEmpty(weightStr)) {
            etWeight.setError("Weight is required");
            etWeight.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(amountStr)) {
            etAmount.setError("Amount is required");
            etAmount.requestFocus();
            return;
        }

        double weight, amount;
        try {
            weight = Double.parseDouble(weightStr);
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (weight <= 0) { etWeight.setError("Weight must be positive"); return; }
        if (amount <= 0) { etAmount.setError("Amount must be positive"); return; }

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        Transaction transaction = new Transaction(sellerId, weight, amount, date, notes.isEmpty() ? "" : notes);

        new Thread(() -> {
            long result = database.transactionDao().insert(transaction);
            if (result > 0) {
                database.sellerDao().updateSellerTotals(sellerId, weight, amount);
            }
            runOnUiThread(() -> {
                if (result > 0) {
                    Toast.makeText(this, "Transaction saved!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to save transaction.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
