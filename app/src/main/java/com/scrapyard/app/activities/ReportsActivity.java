package com.scrapyard.app.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scrapyard.app.R;
import com.scrapyard.app.adapters.ReportSellerAdapter;
import com.scrapyard.app.database.AppDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {

    private AppDatabase database;
    private TextView tvTotalSellers, tvTotalTransactions, tvTotalWeight, tvTotalAmount;
    private RecyclerView recyclerViewTopSellers;
    private ReportSellerAdapter reportAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        database = AppDatabase.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reports & Summary");
        }

        tvTotalSellers = findViewById(R.id.tvTotalSellers);
        tvTotalTransactions = findViewById(R.id.tvTotalTransactions);
        tvTotalWeight = findViewById(R.id.tvTotalWeight);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        recyclerViewTopSellers = findViewById(R.id.recyclerViewTopSellers);

        reportAdapter = new ReportSellerAdapter(new ArrayList<>());
        recyclerViewTopSellers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTopSellers.setAdapter(reportAdapter);

        loadReports();

        database.sellerDao().getAllSellers().observe(this, sellers -> {
            reportAdapter.updateList(sellers);
        });
    }

    private void loadReports() {
        new Thread(() -> {
            int sellerCount = database.sellerDao().getSellersCount();
            int txCount = database.transactionDao().getTransactionsCount();
            double totalWeight = database.transactionDao().getTotalWeight();
            double totalAmount = database.transactionDao().getTotalAmount();

            runOnUiThread(() -> {
                tvTotalSellers.setText(String.valueOf(sellerCount));
                tvTotalTransactions.setText(String.valueOf(txCount));
                tvTotalWeight.setText(String.format(Locale.getDefault(), "%.2f kg", totalWeight));
                tvTotalAmount.setText(String.format(Locale.getDefault(), "R %.2f", totalAmount));
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadReports();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
