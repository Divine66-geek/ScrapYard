package com.scrapyard.app.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageUtils {

    public static final int TARGET_SIZE = 512; // max width/height in pixels
    public static final int JPEG_QUALITY = 85;

    /**
     * Creates a temp File in external Pictures/ScrapYard/ for the camera to write into.
     */
    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "SELLER_" + timeStamp;
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/ScrapYard");
        if (storageDir != null && !storageDir.exists()) {
            storageDir.mkdirs();
        }
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * Returns a content:// Uri for a File using FileProvider.
     */
    public static Uri getUriForFile(Context context, File file) {
        return FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                file
        );
    }

    /**
     * Copies a gallery Uri into the app's private storage and returns the saved file path.
     * Returns null on failure.
     */
    public static String copyGalleryImageToStorage(Context context, Uri sourceUri) {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/ScrapYard");
            if (storageDir != null && !storageDir.exists()) storageDir.mkdirs();

            File destFile = new File(storageDir, "SELLER_" + timeStamp + ".jpg");

            InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) return null;

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap == null) return null;

            bitmap = resizeAndCompress(bitmap, TARGET_SIZE);
            saveBitmapToFile(bitmap, destFile);
            bitmap.recycle();

            return destFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Compresses the camera-taken photo in place and returns the file path.
     */
    public static String compressCameraPhoto(String filePath) {
        try {
            Bitmap bitmap = decodeSampledBitmap(filePath, TARGET_SIZE, TARGET_SIZE);
            if (bitmap == null) return filePath;

            bitmap = correctRotation(bitmap, filePath);
            File file = new File(filePath);
            saveBitmapToFile(bitmap, file);
            bitmap.recycle();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
            return filePath;
        }
    }

    /**
     * Loads a seller photo into an ImageView efficiently.
     * Falls back to the orange placeholder if path is null or file missing.
     */
    public static void loadSellerPhoto(Context context, String photoPath, ImageView imageView) {
        if (photoPath == null || photoPath.isEmpty()) {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }
        File file = new File(photoPath);
        if (!file.exists()) {
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
            return;
        }
        new Thread(() -> {
            Bitmap bitmap = decodeSampledBitmap(photoPath, TARGET_SIZE, TARGET_SIZE);
            if (bitmap != null) {
                Bitmap corrected = correctRotation(bitmap, photoPath);
                ((android.app.Activity) context).runOnUiThread(() -> imageView.setImageBitmap(corrected));
            }
        }).start();
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private static Bitmap decodeSampledBitmap(String path, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private static Bitmap resizeAndCompress(Bitmap original, int maxSize) {
        int width = original.getWidth();
        int height = original.getHeight();
        float scale = Math.min((float) maxSize / width, (float) maxSize / height);
        if (scale >= 1f) return original;
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);
        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
    }

    private static Bitmap correctRotation(Bitmap bitmap, String filePath) {
        try {
            ExifInterface exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:  matrix.postRotate(90);  break;
                case ExifInterface.ORIENTATION_ROTATE_180: matrix.postRotate(180); break;
                case ExifInterface.ORIENTATION_ROTATE_270: matrix.postRotate(270); break;
                default: return bitmap;
            }
            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return rotated;
        } catch (Exception e) {
            return bitmap;
        }
    }

    private static void saveBitmapToFile(Bitmap bitmap, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, fos);
        fos.flush();
        fos.close();
    }
}
