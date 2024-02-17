package com.example.albumgallery;

import static java.sql.DriverManager.println;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "album_gallery.db";
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_TABLE = "Users";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DATABASE_TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT, "
                + "email TEXT, "
                + "password TEXT);");
       println("Table Created");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
    }

    public void insert(String name, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO " + DATABASE_TABLE + " (name, email, password) VALUES ('" + name + "', '" + email + "', '" + password + "');");
    }
}
