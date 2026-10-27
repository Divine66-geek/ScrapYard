package com.scrapyard.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scrapyard.app.R;
import com.scrapyard.app.adapters.SellerAdapter;
import com.scrapyard.app.database.AppDatabase;
import com.scrapyard.app.database.entities.Seller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SellerAdapter.OnSellerClickListener {

    private RecyclerView recyclerViewSellers;
    private SellerAdapter sellerAdapter;
    private AppDatabase database;

    private TextView tvSellerCount;
    private TextView tvTransactionCount;
    private TextView tvTotalWeight;
    private EditText etSearch;
    private FloatingActionButton fabAddSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getInstance(this);
        initViews();
        setupRecyclerView();
        setupSearch();
        loadDashboardStats();
        observeSellers();

        fabAddSeller.setOnClickListener(v -> {
            startActivity(new Intent(this, AddSellerActivity.class));
        });

        findViewById(R.id.btnReports).setOnClickListener(v -> {
            startActivity(new Intent(this, ReportsActivity.class));
        });

        findViewById(R.id.btnSellers).setOnClickListener(v -> {
            startActivity(new Intent(this, SellerListActivity.class));
        });
    }

    private void initViews() {
        tvSellerCount = findViewById(R.id.tvSellerCount);
        tvTransactionCount = findViewById(R.id.tvTransactionCount);
        tvTotalWeight = findViewById(R.id.tvTotalWeight);
        etSearch = findViewById(R.id.etSearch);
        fabAddSeller = findViewById(R.id.fabAddSeller);
        recyclerViewSellers = findViewById(R.id.recyclerViewSellers);
    }

    private void setupRecyclerView() {
        sellerAdapter = new SellerAdapter(new ArrayList<>(), this);
        recyclerViewSellers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSellers.setAdapter(sellerAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    observeSellers();
                } else {
                    database.sellerDao().searchSellers(query).observe(MainActivity.this, sellers -> {
                        sellerAdapter.updateList(sellers);
                    });
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void observeSellers() {
        database.sellerDao().getAllSellers().observe(this, sellers -> {
            sellerAdapter.updateList(sellers);
            updateStatCards(sellers);
        });
    }

    private void loadDashboardStats() {
        new Thread(() -> {
            int sellerCount = database.sellerDao().getSellersCount();
            int txCount = database.transactionDao().getTransactionsCount();
            double totalWeight = database.transactionDao().getTotalWeight();

            runOnUiThread(() -> {
                tvSellerCount.setText(String.valueOf(sellerCount));
                tvTransactionCount.setText(String.valueOf(txCount));
                tvTotalWeight.setText(String.format(Locale.getDefault(), "%.1f kg", totalWeight));
            });
        }).start();
    }

    private void updateStatCards(List<Seller> sellers) {
        tvSellerCount.setText(String.valueOf(sellers.size()));
        new Thread(() -> {
            int txCount = database.transactionDao().getTransactionsCount();
            double totalWeight = database.transactionDao().getTotalWeight();
            runOnUiThread(() -> {
                tvTransactionCount.setText(String.valueOf(txCount));
                tvTotalWeight.setText(String.format(Locale.getDefault(), "%.1f kg", totalWeight));
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardStats();
    }

    @Override
    public void onSellerClick(Seller seller) {
        Intent intent = new Intent(this, SellerDetailActivity.class);
        intent.putExtra("SELLER_ID", seller.getId());
        intent.putExtra("SELLER_NAME", seller.getName());
        startActivity(intent);
    }

    @Override
    public void onNewTransactionClick(Seller seller) {
        Intent intent = new Intent(this, AddTransactionActivity.class);
        intent.putExtra("SELLER_ID", seller.getId());
        intent.putExtra("SELLER_NAME", seller.getName());
        startActivity(intent);
    }
}
