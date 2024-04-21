package com.example.albumgallery.controller;

import com.example.albumgallery.helper.DatabaseHelper;
import com.example.albumgallery.model.ImageAlbumModel;
import com.example.albumgallery.model.Model;

import android.content.Context;
import android.util.Log;

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
        List<String> data = dbHelper.getAll("Image_Album");
        Log.d("check delete data", String.valueOf(data));
        List<ImageAlbumModel> ImageAlbumModels = new ArrayList<>();
        for (String s : data) {
            String[] temp = s.split(",");
            ImageAlbumModels.add(new ImageAlbumModel(temp[0], temp[1]));
        }
        return ImageAlbumModels;
    }
    @Override
    public void insert(Model model) {
//        dbHelper.insert("Image_Album", model);
        dbHelper.insertByCustomId("Image_Album", model);
        dbHelper.close();
    }

    @Override
    public void update(String column, String value, String where) {
        dbHelper.update("Image_Album", column, value, where);
        dbHelper.close();
    }

    @Override
    public void delete(String where) {
        dbHelper.delete("Image_Album", where);
        dbHelper.close();
    }

    public void addImageAlbum(String id_image, String id_album) {
        // Add an ImageAlbum
        ImageAlbumModel imageAlbumModel = new ImageAlbumModel(id_image, id_album);
        this.insert((imageAlbumModel));
    }

    public List<String> getImageIdsByAlbumId(String albumId) {
        return dbHelper.getImageIdsByAlbumId(albumId);
    }

    public void deleteImageAlbum(String id_album) {
        // Delete an ImageAlbum
        this.delete("album_id = " + id_album);
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
