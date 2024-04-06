package com.example.albumgallery.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class QRCodeRecognization extends AsyncTask<String, Void, String> {
    private static final int MAX_BITMAP_DIMENSION = 2048; // Maximum dimension for resizing the image with size if 1024x1024 pixels

    @Override
    protected String doInBackground(String... urls) {
        String uri = urls[0];
        Bitmap bitmap = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap != null) {
                return recognizeQRCode(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // This method is used to calculate the sample size of the image which is used to reduce the size of the image
    private Bitmap downloadImage(String uri) {
        try {
            InputStream in = new URL(uri).openStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateInSampleSize(options, 1024, 1024); // Adjust this to a suitable size
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
            // Continue with your QR code recognition here
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.v("Image Size", "Root size: " + height + "x" + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        Log.v("Image Size", "Sample size: " + inSampleSize);
        return inSampleSize;
    }

    private String recognizeQRCode(Bitmap bitmap) {
        bitmap = resizeBitmap(bitmap);
        // Recognize QR code from the bitmap
        try {
            int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

            MultiFormatReader reader = new MultiFormatReader();
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels)));

            Result result = reader.decode(binaryBitmap);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap resizeBitmap(Bitmap bitmap){
        Log.v("Image Size", "Original size: " + bitmap.getHeight() + "x" + bitmap.getWidth());
        float scaleFactor = Math.min((float) MAX_BITMAP_DIMENSION / bitmap.getWidth(),
                (float) MAX_BITMAP_DIMENSION / bitmap.getHeight());

        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);
        Log.v("Image Size", "Scale factor: " + scaleFactor);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Log.d("QRCodeRecognition", "QR Code data: " + result);
        } else {
            Log.d("QRCodeRecognition", "Failed to recognize QR code");
        }
    }
}