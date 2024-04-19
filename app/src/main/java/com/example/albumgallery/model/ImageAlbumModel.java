package com.example.albumgallery.model;

import android.content.Context;
import android.util.Log;

import com.example.albumgallery.helper.DatabaseHelper;

public class ImageAlbumModel implements Model {
    String image_id;
    long album_id;
    DatabaseHelper dbHelper;

    public String getImage_id() {
        return image_id;
    }

    public void setDbHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(long album_id) {
        this.album_id = album_id;
    }

    public ImageAlbumModel(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public ImageAlbumModel(String image_id, long album_id) {
        this.image_id = image_id;
        this.album_id = album_id;
    }

    @Override
    public String insert() {
        String query = "INSERT INTO Image_Album (image_id, album_id) VALUES (" + image_id + ", " + album_id + ")";
        Log.d("insert image album", query);
        return query;
    }

    @Override
    public void delete() {

    }

    @Override
    public void update() {

    }

    @Override
    public void select() {

    }

    @Override
    public String getId() {
        return ";";
    }
}