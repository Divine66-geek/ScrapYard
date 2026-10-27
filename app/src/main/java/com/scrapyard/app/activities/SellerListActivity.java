package com.scrapyard.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scrapyard.app.R;
import com.scrapyard.app.adapters.SellerAdapter;
import com.scrapyard.app.database.AppDatabase;
import com.scrapyard.app.database.entities.Seller;

import java.util.ArrayList;

public class SellerListActivity extends AppCompatActivity implements SellerAdapter.OnSellerClickListener {

    private RecyclerView recyclerView;
    private SellerAdapter adapter;
    private AppDatabase database;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_list);

        database = AppDatabase.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("All Sellers");
        }

        recyclerView = findViewById(R.id.recyclerViewSellers);
        tvEmpty = findViewById(R.id.tvEmpty);
        FloatingActionButton fab = findViewById(R.id.fabAddSeller);

        adapter = new SellerAdapter(new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> startActivity(new Intent(this, AddSellerActivity.class)));

        database.sellerDao().getAllSellers().observe(this, sellers -> {
            adapter.updateList(sellers);
            tvEmpty.setVisibility(sellers.isEmpty() ? View.VISIBLE : View.GONE);
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
