package com.example.albumgallery.utils;

import static java.text.DateFormat.getDateTimeInstance;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap byteArrayToBitmap(byte[] byteArray) {
        try {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap imageToBitmap(String uri) {
        try {
            URL url = new URL(uri);
            return BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Uri bitmapToUri(Bitmap bitmap, Context context) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
            return Uri.parse(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getImageAddedDate() {
        long currentTime = new Date().getTime();
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
    }

    public static boolean showAlertDialog(Context context, String title, String message, String btnYes, String btnNo) {
        final boolean[] result = {false};
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnYes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result[0] = true;
                    }
                })
                .setNegativeButton(btnNo, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        result[0] = false;
                    }
                })
                .show();
        return result[0];
    }
}
