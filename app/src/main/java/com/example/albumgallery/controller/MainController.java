package com.example.albumgallery.controller;

import android.app.Activity;

import com.example.albumgallery.DatabaseHelper;
import com.example.albumgallery.model.Model;

import java.util.List;

public class MainController {
    private final DatabaseHelper dbHelper;
    private final AlbumController albumController;
    private final ImageController imageController;

    public MainController(Activity activity) {
        dbHelper = new DatabaseHelper(activity);
        albumController = new AlbumController(activity);
        imageController = new ImageController(activity);
    }

    public AlbumController getAlbumController() {
        return albumController;
    }

    public ImageController getImageController() {
        return imageController;
    }
    public List<String> getImagePaths() {
        return imageController.getImagePaths();
    }

}
