package com.example.albumgallery.controller;

import com.example.albumgallery.DatabaseHelper;
import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.model.AlbumModel;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.model.Model;
import com.example.albumgallery.model.auth.ImageAlbumModel;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ImageAlbumController implements Controller {
    private ImageAlbumModel ImageAlbum;
    private final DatabaseHelper dbHelper;

    public ImageAlbumController(Context context) {
        ImageAlbum = new ImageAlbumModel(context);
        this.dbHelper = new DatabaseHelper(context);
    }
    private DatabaseHelper getDbHelper(){
        return dbHelper;
    }
    public List<ImageAlbumModel> getAllImageAlbums() {
        // Get all ImageAlbums
        List<String> data = dbHelper.getAll("ImageAlbum");
        List<ImageAlbumModel> ImageAlbumModels = new ArrayList<>();
        for (String s : data) {
            String[] temp = s.split(",");
            ImageAlbumModels.add(new ImageAlbumModel(Integer.parseInt(temp[0]), Integer.parseInt(temp[1])));
        }
        return ImageAlbumModels;
    }
    @Override
    public void insert(Model model) {
        dbHelper.insert("ImageAlbum", model);
        dbHelper.close();
    }

    @Override
    public void update(String column, String value, String where) {
        dbHelper.update("ImageAlbum", column, value, where);
        dbHelper.close();
    }

    @Override
    public void delete(String where) {
        dbHelper.delete("ImageAlbum", where);
        dbHelper.close();
    }

    public void addImageAlbum(int id_image, int id_album) {
        // Add an ImageAlbum
        ImageAlbumModel imageAlbumModel = new ImageAlbumModel(id_image, id_album);
        this.insert((imageAlbumModel));
    }

    public void deleteImageAlbum() {
        // Delete an ImageAlbum
    }

    public void editImageAlbum() {
        // Edit an ImageAlbum
    }

    public void viewImageAlbum() {
        // View an ImageAlbum
    }

    public void shareImageAlbum() {
        // Share an ImageAlbum
    }

    public void favouriteImageAlbum() {
        // Favourite an ImageAlbum
    }

    public void unfavouriteImageAlbum() {
        // Unfavourite an ImageAlbum
    }
}
