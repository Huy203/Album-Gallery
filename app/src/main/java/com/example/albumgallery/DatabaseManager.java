package com.example.albumgallery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.albumgallery.helper.DatabaseHelper;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private final Context context;

    public DatabaseManager(Context ctx) {
        context = ctx;
    }

    public void open() throws SQLiteException {
        dbHelper = new DatabaseHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        if (database != null) {
            Log.e("Database Manager", "Database connected");
        } else {
            Log.e("Database Manager", "Failed to connect to database");
        }
    }
}
