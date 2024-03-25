package com.example.albumgallery.controller;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.example.albumgallery.DatabaseHelper;
import com.example.albumgallery.model.Model;

public class MainController {
    private final AlbumController albumController;
    private final ImageController imageController;

    public MainController(Activity activity) {
        albumController = new AlbumController(activity);
        imageController = new ImageController(activity);
    }


    public AlbumController getAlbumController() {
        return albumController;
    }

    public ImageController getImageController() {
        return imageController;
    }
}
