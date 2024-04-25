package com.example.albumgallery.helper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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
    public static final String DATABASE_NAME = "AlbumGallery.db";
    public static final int DATABASE_VERSION = 1;
    public static final String USER_TABLE = "User";
    public static final String ALBUM_TABLE = "Album";
    public static final String IMAGE_TABLE = "Image";
    public static final String IMAGE_ALBUM_TABLE = "Image_Album";

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
            db.execSQL("CREATE TABLE " + USER_TABLE + " (\n" +
                    "    id TEXT,\n" +
                    "    username TEXT,\n" +
                    "    email TEXT,\n" +
                    "    phone TEXT,\n" +
                    "    created_at TIMESTAMP,\n" +
                    "    birth TIMESTAMP,\n" +
                    "    picture TEXT\n" +
                    ");");
            db.execSQL("CREATE TABLE " + ALBUM_TABLE + " (\n" +
                    "    id TEXT PRIMARY KEY,\n" +
                    "    name TEXT,\n" +
                    "    capacity INTEGER,\n" +
                    "    created_at TIMESTAMP,\n" +
                    "    notice TEXT,\n" +
                    "    ref TEXT,\n" +
                    "    is_deleted INTEGER,\n" +
                    "    num_of_images INTEGER,\n" +
                    "    password TEXT, \n" +
                    "    thumbnail TEXT\n" +
                    ");");
            db.execSQL("CREATE TABLE " + IMAGE_TABLE + " (\n" +
                    "    id TEXT,\n" +
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
                    "    image_id TEXT REFERENCES " + IMAGE_TABLE + "(id),\n" +
                    "    album_id TEXT REFERENCES " + ALBUM_TABLE + "(id),\n" +
                    "    PRIMARY KEY (image_id, album_id)\n" +
                    ");");
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ALBUM_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + IMAGE_ALBUM_TABLE);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public String insert(String table, Model model) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(model.insert());
        return model.getId();
    }

    public void insertByCustomId(String table, Model model) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.execSQL(model.insert());
        } catch (SQLiteException e) {
            Log.e("Database Error", e.getMessage());
        }
    }

    public void delete(String table, String where) {
        SQLiteDatabase db = getWritableDatabase();
        if (where == null) {
            db.execSQL("DELETE FROM " + table);
        } else {
            db.execSQL("DELETE FROM " + table + " WHERE " + where);
        }
    }

    public void update(String table, String column, String value, String where) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + table + " SET " + column + " = " + "'" + value + "'" + " WHERE " + where);
    }

    public List<String> select(String table, String column, String where) {
        List<String> data = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = (column == null)
                ? (where == null)
                ? db.rawQuery("SELECT * FROM " + table, null)
                : db.rawQuery("SELECT * FROM " + table + " WHERE " + where, null)
                : (where == null)
                ? db.rawQuery("SELECT " + column + " FROM " + table, null)
                : db.rawQuery("SELECT " + column + " FROM " + table + " WHERE " + where, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String temp = cursor.getString(0) + "," + cursor.getString(1) + "," + cursor.getString(2) + "," + cursor.getString(3) + "," + cursor.getString(4) + "," + cursor.getString(5) + "," + cursor.getString(6) + "," + cursor.getString(7) + "," + cursor.getString(8) + "," + cursor.getString(9) + "," + cursor.getString(10);
                data.add(temp);
                cursor.moveToNext();
            }
        } else {
            Log.e("DatabaseHelper", "No data found");
        }
        cursor.close();
        return data;
    }

    public List<String> selectImagesByNotice(String notice) {
        if (notice.isEmpty()) {
            return null;
        } else {
            Log.d("notice is not null", notice);
        }
        List<String> imagePaths = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;

        try {
            // Define the query to select image paths by notice
            String query = "SELECT ref FROM " + IMAGE_TABLE + " WHERE notice LIKE ? AND is_deleted = 0";

            String likeNotice = notice + "%";

            // Execute the query
            cursor = db.rawQuery(query, new String[]{likeNotice});

            // Iterate through the cursor to retrieve the image paths
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Get the image path from the cursor
                    @SuppressLint("Range") String imagePath = cursor.getString(cursor.getColumnIndex("ref"));
                    imagePaths.add(imagePath);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error selecting images by notice: " + e.getMessage());
        } finally {
            // Close the cursor and database connection
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return imagePaths;
    }

    public List<String> selectImageNamesByNotice(String notice) {
        if (notice.isEmpty()) {
            Log.d("notice is null", "ok");
            return null;
        } else {
            Log.d("notice is not null", notice);
        }
        List<String> imageNames = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = null;

        try {
            // Define the query to select image names by notice
            String query = "SELECT name FROM " + IMAGE_TABLE + " WHERE notice LIKE ? AND is_deleted = 0";

            // Concatenate the notice text with a wildcard to match any prefix
            String likeNotice = notice + "%";

            // Execute the query
            cursor = db.rawQuery(query, new String[]{likeNotice});

            // Iterate through the cursor to retrieve the image paths
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Get the image path from the cursor
                    @SuppressLint("Range") String imageName = cursor.getString(cursor.getColumnIndex("name"));
                    imageNames.add(imageName);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error selecting images by notice: " + e.getMessage());
        } finally {
            // Close the cursor and database connection
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        Log.d("image name find by notice", String.valueOf(imageNames.size()));
        return imageNames;
    }


    public List<String> selectImagesSortByDateAtBin(String table, String column, String order) {
        List<String> data = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + column + " FROM " + table + " WHERE is_deleted = 1 ORDER BY created_at";
        if (Objects.equals(order, "ascending")) {
            query += " ASC;";
        } else if ((Objects.equals(order, "descending"))) {
            query += " DESC;";
        }
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                data.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            Log.v("DatabaseHelper", "no data found");
        }
        return data;
    }

    public String getId(String table, String where) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + table + " WHERE " + where, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        return null;
    }

    public String getById(String table, String id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE id = '" + id + "'", null);
        if (cursor.moveToFirst()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                stringBuilder.append(cursor.getString(i));
                if (i < cursor.getColumnCount() - 1) {
                    stringBuilder.append(",");
                }
            }
            return stringBuilder.toString();
        }
        return null;
    }

    public String getLastId(String table) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + table + " ORDER BY ROWID DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        return null;
    }

    public List<String> getAll(String table) {
        List<String> data = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table, null);
        if (cursor.moveToFirst()) {
            int numColumns = cursor.getColumnCount();
            do {
                StringBuilder temp = new StringBuilder();
                for (int i = 0; i < numColumns; i++) {
                    temp.append(cursor.getString(i));
                    if (i < numColumns - 1) {
                        temp.append(",");
                    }
                }
                data.add(temp.toString());
            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    public List<String> getAllRef(String table, String where) {
        List<String> data = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor;
        if (where == null) {
            cursor = db.rawQuery("SELECT ref FROM " + table, null);
        } else {
            cursor = db.rawQuery("SELECT ref FROM " + table + " WHERE " + where, null);
        }
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                data.add(cursor.getString(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return data;
    }

    public List<String> getFromAlbum(String column) {
        List<String> res = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + column + " FROM " + ALBUM_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(0);
                res.add(name);
                cursor.moveToNext();
            }
        }
        return res;
    }

    public List<String> getFromImage(String column) {
        List<String> res = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT " + column + " FROM " + IMAGE_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(0);
                res.add(name);
                cursor.moveToNext();
            }
        }
        return res;
    }

    public boolean isAlbumNameExists(String albumName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + ALBUM_TABLE + " WHERE name = ?", new String[]{albumName});
        int count = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }
        return count > 0;
    }

    public String getAlbumIdByName(String albumName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + ALBUM_TABLE + " WHERE name = ?", new String[]{albumName});
        String albumId = "";
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                albumId = cursor.getString(0);
            }
            cursor.close();
        }
        return albumId;
    }

    public List<String> getImageIdsByAlbumId(String albumId) {
        List<String> imageIds = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT image_id FROM " + IMAGE_ALBUM_TABLE + " WHERE album_id = ?", new String[]{String.valueOf(albumId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String imageId = cursor.getString(0);
                    imageIds.add(imageId);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return imageIds;
    }

    public String getImageRefById(String imageId) {
        SQLiteDatabase db = getReadableDatabase();
        String ref = null;
        Cursor cursor = db.rawQuery("SELECT ref FROM " + IMAGE_TABLE + " WHERE id = ?", new String[]{String.valueOf(imageId)});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ref = cursor.getString(0);
            }
            cursor.close();
        }
        return ref;
    }

    @SuppressLint("Range")
    public long getTotalCapacityFromImageIDs(List<String> imageIDs) {
        long totalCapacity = 0;
        SQLiteDatabase db = getReadableDatabase();

        for (String imageId : imageIDs) {
            Cursor cursor = db.rawQuery("SELECT capacity FROM " + IMAGE_TABLE + " WHERE id = ?", new String[]{String.valueOf(imageId)});
            if (cursor != null && cursor.moveToFirst()) {
                // Get the capacity from the cursor and add it to the total capacity
                long capacity = cursor.getLong(cursor.getColumnIndex("capacity"));
                totalCapacity += capacity;
                cursor.close();
            }
        }
        return totalCapacity;
    }

    public void setDelete(String imageId, boolean isDelete) {
        SQLiteDatabase db = getWritableDatabase();
        int delete = isDelete ? 1 : 0;
        ContentValues values = new ContentValues();
        values.put("is_deleted", delete);
        db.update(IMAGE_TABLE, values, "id = ?", new String[]{String.valueOf(imageId)});
    }

    public String getImageIdByURL(String URL) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + IMAGE_TABLE + " WHERE ref = ?", new String[]{URL});
        String imageId = null;
        if (cursor != null) {
            Log.d("cursor", cursor.toString());
            if (cursor.moveToFirst()) {
                imageId = cursor.getString(0);
                Log.d("album id", String.valueOf(imageId));
            }
            cursor.close();
        }
        return imageId;
    }

    public String getPasswordByAlbumName(String albumName) {
        SQLiteDatabase db = getReadableDatabase();
        String password = null;

        Cursor cursor = db.rawQuery("SELECT password FROM " + ALBUM_TABLE + " WHERE name = ?", new String[]{albumName});
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                password = cursor.getString(0);
            }
            cursor.close();
        }
        return password;
    }

    public List<String> getAllThumbnails() {
        List<String> thumbnails = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT thumbnail FROM " + ALBUM_TABLE, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String thumbnail = cursor.getString(0);
                    thumbnails.add(thumbnail);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        return thumbnails;
    }

    public void removeAlbumPasswordByName(String albumName) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", "");
        db.update(ALBUM_TABLE, values, "name = ?", new String[]{albumName});
    }
}
