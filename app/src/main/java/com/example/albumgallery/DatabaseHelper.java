package com.example.albumgallery;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.albumgallery.model.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DatabaseHelper extends SQLiteOpenHelper {
    //    public static final String DATABASE_NAME = "album_gallery.db";
    public static final String DATABASE_NAME = "AlbumGallery.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TAG_TABLE = "Tag";
    public static final String SIZE_TABLE = "Size";
    public static final String USERS_TABLE = "Users";
    public static final String ALBUM_TABLE = "Album";
    public static final String IMAGE_TABLE = "Image";
    public static final String IMAGE_ALBUM_TABLE = "Image_Album";
    public static final String IMAGE_TAG_TABLE = "Image_Tag";
    public static final String VIDEO_TABLE = "Video";
    public static final String VIDEO_ALBUM_TABLE = "Video_Album";
    public static final String VIDEO_TAG_TABLE = "Video_Tag";
    public static final String TRASH_TABLE = "Trash";
    public static final String IMAGE_TRASH_TABLE = "Image_Trash";
    public static final String VIDEO_TRASH_TABLE = "Video_Trash";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        initialize_all_schemas(db);
        try {
            db.execSQL("DROP TABLE IF EXISTS android_metadata");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private void initialize_all_schemas(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + TAG_TABLE + " (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    name TEXT\n" +
                    ");");
//            db.execSQL("CREATE TABLE " + SIZE_TABLE + " (\n" +
//                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
//                    "    width REAL,\n" +
//                    "    height REAL\n" +
//                    ");");
            db.execSQL("CREATE TABLE " + USERS_TABLE + " (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    username TEXT,\n" +
                    "    password TEXT,\n" +
                    "    picture TEXT\n" +
                    ");");
            db.execSQL("CREATE TABLE " + ALBUM_TABLE + " (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    name TEXT,\n" +
                    "    capacity INTEGER,\n" +
                    "    created_at TIMESTAMP,\n" +
                    "    notice TEXT,\n" +
                    "    ref TEXT,\n" +
                    "    is_deleted INTEGER,\n" +
                    "    num_of_images INTEGER,\n" +
                    "    password TEXT\n" +
                    ");");
            db.execSQL("CREATE TABLE " + IMAGE_TABLE + " (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    name TEXT,\n" +
                    "    width REAL,\n" +
                    "    height REAL,\n" +
                    "    capacity INTEGER,\n" +
                    "    created_at TIMESTAMP,\n" +
                    "    notice TEXT,\n" +
                    "    ref TEXT,\n" +
                    "    remain_time TEXT,\n" +
                    "    is_deleted INTEGER,\n" +
                    "    is_favourited INTEGER\n" +
                    ");");
            db.execSQL("CREATE TABLE " + IMAGE_ALBUM_TABLE + "(\n" +
                    "    image_id INTEGER REFERENCES " + IMAGE_TABLE + "(id),\n" +
                    "    album_id INTEGER REFERENCES " + ALBUM_TABLE + "(id),\n" +
                    "    PRIMARY KEY (image_id, album_id)\n" +
                    ");");
            db.execSQL("CREATE TABLE " + IMAGE_TAG_TABLE + "(\n" +
                    "    image_id INTEGER REFERENCES "   + IMAGE_TABLE + "(id),\n" +
                    "    tag_id INTEGER REFERENCES " + TAG_TABLE + "(id),\n" +
                    "    PRIMARY KEY (image_id, tag_id)\n" +
                    ");");
            db.execSQL("CREATE TABLE " + VIDEO_TABLE + " (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    name TEXT,\n" +
                    "    width REAL,\n" +
                    "    height REAL,\n" +
                    "    capacity INTEGER,\n" +
                    "    created_at TIMESTAMP,\n" +
                    "    notice TEXT,\n" +
                    "    ref TEXT,\n" +
                    "    remain_time TEXT,\n" +
                    "    is_deleted INTEGER,\n" +
                    "    is_favourited INTEGER\n" +
                    ");");
            db.execSQL("CREATE TABLE " + VIDEO_ALBUM_TABLE + "(\n" +
                    "    video_id INTEGER REFERENCES " + VIDEO_TABLE + "(id),\n" +
                    "    album_id INTEGER REFERENCES " + ALBUM_TABLE + "(id)\n" +
                    ");");
            db.execSQL("CREATE TABLE " + VIDEO_TAG_TABLE + "(\n" +
                    "    video_id INTEGER REFERENCES " + VIDEO_TABLE + "(id),\n" +
                    "    tag_id INTEGER REFERENCES " + TAG_TABLE + "(id)\n" +
                    ");");
            db.execSQL("CREATE TABLE " + TRASH_TABLE + "(\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    capacity INTEGER\n" +
                    ");");
            db.execSQL("CREATE TABLE " + IMAGE_TRASH_TABLE + "(\n" +
                    "    image_id INTEGER REFERENCES " + IMAGE_TABLE + "(id),\n" +
                    "    trash_id INTEGER REFERENCES " + TRASH_TABLE + "(id)\n" +
                    ");");
            db.execSQL("CREATE TABLE " + VIDEO_TRASH_TABLE + "(\n" +
                    "    video_id INTEGER REFERENCES " + VIDEO_TABLE + "(id),\n" +
                    "    trash_id INTEGER REFERENCES " + TRASH_TABLE + "(id)\n" +
                    ");");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TAG_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + SIZE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ALBUM_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + IMAGE_ALBUM_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TAG_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + VIDEO_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + VIDEO_ALBUM_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + VIDEO_TAG_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + TRASH_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TRASH_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + VIDEO_TRASH_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public long insert(String table, Model model) {
        Log.v("DatabaseHelper", "Inserting data");
        SQLiteDatabase db = getWritableDatabase();
        Log.v("DatabaseHelper", "LastID: "+ getLastId(table));
        db.execSQL(model.insert());
        Log.v("DatabaseHelper", "LastID: "+ getLastId(table));
        return getLastId(table);
    }

    public void delete(String table, String where){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + table + " WHERE " + where);
    }

    public void update(String table, String column, String value, String where) {
        Log.v("DatabaseHelper", "Updating data");
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + table + " SET " + column + " = " + "'" + value + "'" + " WHERE " + where);
    }

    public List<String> select(String table, String column, String where) {
        List<String> data = new ArrayList<>();
        Log.v("DatabaseHelper", "Selecting data");
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = (column == null)
                ? (where == null)
                ? db.rawQuery("SELECT * FROM " + table, null)
                : db.rawQuery("SELECT * FROM " + table + " WHERE " + where, null)
                : (where == null)
                ? db.rawQuery("SELECT " + column + " FROM " + table, null)
                : db.rawQuery("SELECT " + column + " FROM " + table + " WHERE " + where, null);
        Log.d("hello",cursor.toString());
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String temp = cursor.getString(0) + "," + cursor.getString(1) + "," + cursor.getString(2)+ "," + cursor.getString(3)+ "," + cursor.getString(4)+ "," + cursor.getString(5)+ "," + cursor.getString(6)+ "," + cursor.getString(7)+ "," + cursor.getString(8)+ "," + cursor.getString(9)+ "," + cursor.getString(10);
                data.add(temp);
                cursor.moveToNext();
            }
        } else {
            Log.v("DatabaseHelper", "No data found");
        }
        cursor.close();
        return data;
    }

    public List<String> selectImagesSortByDate(String table, String column, String order) {
        List<String> data = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + column + " FROM " + table + " ORDER BY created_at";
        if(Objects.equals(order, "ascending")) {
            query += " ASC;";
        } else if ( (Objects.equals(order,"descending"))) {
            query += " DESC;";
        }
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()) {
            while(!cursor.isAfterLast()) {
                data.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            Log.v("DatabaseHelper", "no data found");
        }
        return data;
    }

    public void getConnection() {
        SQLiteDatabase db = getWritableDatabase();
    }

    // Get all columns of a table
    public void getCols(String table) {
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> columns = new ArrayList<>();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + table + ")", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                columns.add(cursor.getString(1));
                cursor.moveToNext();
            }
        }
        Log.v("DatabaseHelper", "Columns: " + table + columns);
        cursor.close();
    }

    public long getId(String table,  String where) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + table + " WHERE " + where, null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return -1;
    }

    public String getById(String table, long id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE id = " + id, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(0) + "," + cursor.getString(1) + "," + cursor.getString(2)+ "," + cursor.getString(3)+ "," + cursor.getString(4)+ "," + cursor.getString(5)+ "," + cursor.getString(6)+ "," + cursor.getString(7)+ "," + cursor.getString(8)+ "," + cursor.getString(9)+ "," + cursor.getString(10);
        }
        return null;
    }

    public long getLastId(String table) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + table + " ORDER BY id DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return -1;
    }

    public List<String> getAll(String table) {
        List<String> data = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String temp = cursor.getString(0) + "," + cursor.getString(1) + "," + cursor.getString(2)+ "," + cursor.getString(3)+ "," + cursor.getString(4)+ "," + cursor.getString(5)+ "," + cursor.getString(6)+ "," + cursor.getString(7)+ "," + cursor.getString(8)+ "," + cursor.getString(9)+ "," + cursor.getString(10);
                data.add(temp);
                cursor.moveToNext();
            }
        }
        return data;
    }
    public List<String> getAllRef(String table){
        List<String> data = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT ref FROM " + table, null);
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                data.add(cursor.getString(0));
                cursor.moveToNext();
            }
        }
        return data;
    }
}
