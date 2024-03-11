package com.example.albumgallery;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private Context context;
    private SQLiteDatabase database;

    public DatabaseManager(Context ctx){
        context = ctx;
    }

    public DatabaseManager open() throws SQLiteException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
//        if(database != null) {
//            Log.e("Database Manager", "Database connected");
//        } else {
//            Log.e("Database Manger", "Failed to connect to database");
//        }
        return this;
    }

    public void insert(String name, String email, String password){
        dbHelper.insert(name, email, password);
    }

    public void close(){
        dbHelper.close();
    }
}
