package com.example.albumgallery.model.auth;

import android.content.Context;

import com.example.albumgallery.helper.DatabaseHelper;
import com.example.albumgallery.model.Model;

public class ImageAlbumModel implements Model {
    int image_id;
    int album_id;
    DatabaseHelper dbHelper;

    public int getImage_id() {
        return image_id;
    }

    public void setDbHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }
    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public int getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(int album_id) {
        this.album_id = album_id;
    }

    public ImageAlbumModel(Context context) {
        dbHelper = new DatabaseHelper(context);
    }
    public ImageAlbumModel(int image_id, int album_id){
        this.image_id = image_id;
        this.album_id = album_id;
    }
    @Override
    public String insert() {
        return "INSERT INTO Album (image_id, album_id) VALUES ('" + image_id + ", " + album_id + ", '" + ")";
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
}
