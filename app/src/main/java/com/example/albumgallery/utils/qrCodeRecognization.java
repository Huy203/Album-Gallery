package com.example.albumgallery.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;
import java.net.URL;

public class qrCodeRecognization extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        String imageUrl = urls[0];
        Bitmap bitmap = downloadImage(imageUrl);
        if (bitmap != null) {
            return recognizeQRCode(bitmap);
        }
        return null;
    }

    private Bitmap downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String recognizeQRCode(Bitmap bitmap) {
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

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Log.d("QRCodeRecognition", "QR Code data: " + result);
        } else {
            Log.d("QRCodeRecognition", "Failed to recognize QR code");
        }
    }
}