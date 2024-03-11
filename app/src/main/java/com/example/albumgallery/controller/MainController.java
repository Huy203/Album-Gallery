package com.example.albumgallery.controller;

import android.content.Context;

import com.example.albumgallery.DatabaseHelper;
import com.example.albumgallery.model.Model;

public class MainController {
    private DatabaseHelper dbHelper;
    private AlbumController albumController;
    private ImageController imageController;

    public MainController(Context context) {
        dbHelper = new DatabaseHelper(context);
        albumController = new AlbumController(context);
        imageController = new ImageController(context);
    }
    public void insert (String table, Model model) {
        // Insert data
        dbHelper.insert(table, model);
    }

    public AlbumController getAlbumController() {
        return albumController;
    }

    public ImageController getImageController() {
        return imageController;
    }
}
