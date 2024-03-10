package com.example.albumgallery;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
public class DatabaseHelper extends SQLiteOpenHelper {
//    static final String DATABASE_NAME = "album_gallery.db";
    static final String DATABASE_NAME = "AlbumGallery.db";
    static final int DATABASE_VERSION = 1;
    static final String TAG_TABLE = "Tag";
    static final String SIZE_TABLE = "Size";
    static final String USERS_TABLE = "Users";
    static final String ALBUM_TABLE = "Album";
    static final String IMAGE_TABLE = "Image";
    static final String IMAGE_ALBUM_TABLE = "Image_Album";
    static final String IMAGE_TAG_TABLE = "Image_Tag";
    static final String VIDEO_TABLE = "Video";
    static final String VIDEO_ALBUM_TABLE = "Video_Album";
    static final String VIDEO_TAG_TABLE = "Video_Tag";

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
            db.execSQL("CREATE TABLE " + SIZE_TABLE + " (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    width REAL,\n" +
                    "    height REAL\n" +
                    ");");
            db.execSQL("CREATE TABLE " + USERS_TABLE + " (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    username TEXT,\n" +
                    "    password TEXT,\n" +
                    "    picture TEXT\n" +
                    ");");
            db.execSQL("CREATE TABLE " + ALBUM_TABLE + " (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    name TEXT,\n" +
                    "    id_size INTEGER REFERENCES " + SIZE_TABLE + "(id),\n" +
                    "    capacity INTEGER,\n" +
                    "    created_at TIMESTAMP,\n" +
                    "    notice TEXT,\n" +
                    "    user_id INTEGER,\n" +
                    "    password TEXT\n" +
                    ");");
            db.execSQL("CREATE TABLE " + IMAGE_TABLE + " (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    name TEXT,\n" +
                    "    id_size INTEGER REFERENCES " + SIZE_TABLE + "(id),\n" +
                    "    capacity INTEGER,\n" +
                    "    created_at TIMESTAMP,\n" +
                    "    notice TEXT,\n" +
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
                    "    image_id INTEGER REFERENCES " + IMAGE_TABLE + "(id),\n" +
                    "    tag_id INTEGER REFERENCES " + TAG_TABLE + "(id),\n" +
                    "    PRIMARY KEY (image_id, tag_id)\n" +
                    ");");
            db.execSQL("CREATE TABLE " + VIDEO_TABLE + " (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    name TEXT,\n" +
                    "    size INTEGER REFERENCES " + SIZE_TABLE + "(id),\n" +
                    "    capacity INTEGER,\n" +
                    "    created_at TIMESTAMP,\n" +
                    "    notice TEXT,\n" +
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
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
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
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
    public void insert(String name, String email, String password) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO " + USERS_TABLE + " (name, email, password) VALUES ('" + name + "', '" + email + "', '" + password + "');");
    }
}
