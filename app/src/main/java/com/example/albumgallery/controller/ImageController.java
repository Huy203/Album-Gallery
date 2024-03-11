package com.example.albumgallery.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.albumgallery.DatabaseHelper;
import com.example.albumgallery.DatabaseManager;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.model.Model;

import java.util.List;

public class ImageController {
    private DatabaseHelper dbHelper;

    public ImageController(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void getAll() {
        // Get all images
        Log.v("ImageController", "Getting all images");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Image", null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Log.v("ImageController", "Image: " + cursor.getString(0));
                cursor.moveToNext();
            }
        }
    }

    public void add(Model model) {
        // Add an image
        Log.v("ImageController", "Adding image");
        ContentValues values = new ContentValues();
        Log.v("ImageController", "Adding image");
        dbHelper.insert("Image", model);
        dbHelper.close();
    }
}
