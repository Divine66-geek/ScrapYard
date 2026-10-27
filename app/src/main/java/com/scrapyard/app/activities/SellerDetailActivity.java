package com.scrapyard.app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scrapyard.app.R;
import com.scrapyard.app.adapters.TransactionAdapter;
import com.scrapyard.app.database.AppDatabase;
import com.scrapyard.app.database.entities.Seller;
import com.scrapyard.app.utils.ImageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SellerDetailActivity extends AppCompatActivity {

    private AppDatabase database;
    private int sellerId;
    private Seller currentSeller;

    private ImageView ivSellerPhoto;
    private TextView tvName, tvNationalId, tvTotalWeight, tvTotalAmount, tvTransactionCount, tvEmpty;
    private RecyclerView recyclerViewTransactions;
    private TransactionAdapter transactionAdapter;

    private File cameraPhotoFile;

    // ── Activity Result Launchers ────────────────────────────────────────────

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && cameraPhotoFile != null) {
                    new Thread(() -> {
                        String path = ImageUtils.compressCameraPhoto(cameraPhotoFile.getAbsolutePath());
                        database.sellerDao().updatePhoto(sellerId, path);
                        runOnUiThread(() -> ImageUtils.loadSellerPhoto(this, path, ivSellerPhoto));
                    }).start();
                }
            });

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    new Thread(() -> {
                        String path = ImageUtils.copyGalleryImageToStorage(this, uri);
                        if (path != null) {
                            database.sellerDao().updatePhoto(sellerId, path);
                            runOnUiThread(() -> ImageUtils.loadSellerPhoto(this, path, ivSellerPhoto));
                        } else {
                            runOnUiThread(() -> Toast.makeText(this, "Could not load image", Toast.LENGTH_SHORT).show());
                        }
                    }).start();
                }
            });

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results -> {
                boolean allGranted = true;
                for (Boolean g : results.values()) if (!g) { allGranted = false; break; }
                if (allGranted) showPhotoOptions();
                else Toast.makeText(this, "Permission needed for camera/gallery", Toast.LENGTH_SHORT).show();
            });

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_detail);

        database = AppDatabase.getInstance(this);
        sellerId = getIntent().getIntExtra("SELLER_ID", -1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Seller Details");
        }

        ivSellerPhoto       = findViewById(R.id.ivSellerPhoto);
        tvName              = findViewById(R.id.tvSellerName);
        tvNationalId        = findViewById(R.id.tvNationalId);
        tvTotalWeight       = findViewById(R.id.tvTotalWeight);
        tvTotalAmount       = findViewById(R.id.tvTotalAmount);
        tvTransactionCount  = findViewById(R.id.tvTransactionCount);
        tvEmpty             = findViewById(R.id.tvEmpty);
        recyclerViewTransactions = findViewById(R.id.recyclerViewTransactions);
        Button btnNewTransaction = findViewById(R.id.btnNewTransaction);
        Button btnChangePhoto    = findViewById(R.id.btnChangePhoto);

        transactionAdapter = new TransactionAdapter(new ArrayList<>());
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTransactions.setAdapter(transactionAdapter);

        loadSellerInfo();

        database.transactionDao().getTransactionsBySeller(sellerId).observe(this, transactions -> {
            transactionAdapter.updateList(transactions);
            tvEmpty.setVisibility(transactions.isEmpty() ? View.VISIBLE : View.GONE);
        });

        btnNewTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTransactionActivity.class);
            intent.putExtra("SELLER_ID", sellerId);
            intent.putExtra("SELLER_NAME", tvName.getText().toString());
            startActivity(intent);
        });

        btnChangePhoto.setOnClickListener(v -> checkPermissionsAndOpenPicker());
        ivSellerPhoto.setOnClickListener(v -> checkPermissionsAndOpenPicker());
    }

    private void loadSellerInfo() {
        new Thread(() -> {
            currentSeller = database.sellerDao().getSellerById(sellerId);
            if (currentSeller != null) {
                runOnUiThread(() -> {
                    tvName.setText(currentSeller.getName());
                    tvNationalId.setText("ID: " + currentSeller.getNationalId());
                    tvTotalWeight.setText(String.format(Locale.getDefault(), "%.2f kg", currentSeller.getTotalWeight()));
                    tvTotalAmount.setText(String.format(Locale.getDefault(), "R %.2f", currentSeller.getTotalAmount()));
                    tvTransactionCount.setText(String.valueOf(currentSeller.getTransactionCount()));
                    ImageUtils.loadSellerPhoto(this, currentSeller.getPhotoPath(), ivSellerPhoto);
                });
            }
        }).start();
    }

    // ── Photo Picker ─────────────────────────────────────────────────────────

    private void checkPermissionsAndOpenPicker() {
        String[] perms = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_MEDIA_IMAGES}
                : new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};

        boolean allGranted = true;
        for (String p : perms)
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) { allGranted = false; break; }

        if (allGranted) showPhotoOptions();
        else permissionLauncher.launch(perms);
    }

    private void showPhotoOptions() {
        new AlertDialog.Builder(this)
                .setTitle("Update Seller Photo")
                .setItems(new String[]{"📷  Take Photo", "🖼️  Choose from Gallery"}, (d, which) -> {
                    if (which == 0) openCamera();
                    else galleryLauncher.launch("image/*");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openCamera() {
        try {
            cameraPhotoFile = ImageUtils.createImageFile(this);
            cameraLauncher.launch(ImageUtils.getUriForFile(this, cameraPhotoFile));
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open camera", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSellerInfo();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { finish(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
