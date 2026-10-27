package com.scrapyard.app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.scrapyard.app.R;
import com.scrapyard.app.database.AppDatabase;
import com.scrapyard.app.database.entities.Seller;
import com.scrapyard.app.utils.ImageUtils;

import java.io.File;

public class AddSellerActivity extends AppCompatActivity {

    private EditText etName, etNationalId;
    private ImageView ivSellerPhoto;
    private TextView tvPhotoHint;
    private AppDatabase database;

    private File cameraPhotoFile;        // temp file the camera writes to
    private String savedPhotoPath = null; // final path stored in DB

    // ── Activity Result Launchers ────────────────────────────────────────────

    private final ActivityResultLauncher<Uri> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success && cameraPhotoFile != null) {
                    new Thread(() -> {
                        savedPhotoPath = ImageUtils.compressCameraPhoto(cameraPhotoFile.getAbsolutePath());
                        runOnUiThread(() -> {
                            ImageUtils.loadSellerPhoto(this, savedPhotoPath, ivSellerPhoto);
                            tvPhotoHint.setText("Photo taken ✓");
                        });
                    }).start();
                }
            });

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    new Thread(() -> {
                        savedPhotoPath = ImageUtils.copyGalleryImageToStorage(this, uri);
                        runOnUiThread(() -> {
                            if (savedPhotoPath != null) {
                                ImageUtils.loadSellerPhoto(this, savedPhotoPath, ivSellerPhoto);
                                tvPhotoHint.setText("Photo selected ✓");
                            } else {
                                Toast.makeText(this, "Could not load image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }).start();
                }
            });

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results -> {
                boolean allGranted = true;
                for (Boolean granted : results.values()) {
                    if (!granted) { allGranted = false; break; }
                }
                if (allGranted) showPhotoOptions();
                else Toast.makeText(this, "Permission needed to access camera/gallery", Toast.LENGTH_SHORT).show();
            });

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_seller);

        database = AppDatabase.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New Seller");
        }

        etName       = findViewById(R.id.etName);
        etNationalId = findViewById(R.id.etNationalId);
        ivSellerPhoto = findViewById(R.id.ivSellerPhoto);
        tvPhotoHint  = findViewById(R.id.tvPhotoHint);
        Button btnSave  = findViewById(R.id.btnSave);
        Button btnPhoto = findViewById(R.id.btnAddPhoto);

        btnPhoto.setOnClickListener(v -> checkPermissionsAndOpenPicker());
        ivSellerPhoto.setOnClickListener(v -> checkPermissionsAndOpenPicker());
        btnSave.setOnClickListener(v -> saveSeller());
    }

    // ── Permission & Photo Picker ────────────────────────────────────────────

    private void checkPermissionsAndOpenPicker() {
        String[] perms;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            perms = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }

        boolean allGranted = true;
        for (String p : perms) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) showPhotoOptions();
        else permissionLauncher.launch(perms);
    }

    private void showPhotoOptions() {
        String[] options = {"📷  Take Photo", "🖼️  Choose from Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Seller Photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) openCamera();
                    else openGallery();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openCamera() {
        try {
            cameraPhotoFile = ImageUtils.createImageFile(this);
            Uri photoUri = ImageUtils.getUriForFile(this, cameraPhotoFile);
            cameraLauncher.launch(photoUri);
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    // ── Save Seller ──────────────────────────────────────────────────────────

    private void saveSeller() {
        String name       = etName.getText().toString().trim();
        String nationalId = etNationalId.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(nationalId)) {
            etNationalId.setError("National ID is required");
            etNationalId.requestFocus();
            return;
        }

        Seller seller = new Seller(name, nationalId);
        seller.setPhotoPath(savedPhotoPath);

        new Thread(() -> {
            long result = database.sellerDao().insert(seller);
            runOnUiThread(() -> {
                if (result > 0) {
                    Toast.makeText(this, "Seller added successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to add seller.", Toast.LENGTH_SHORT).show();
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
