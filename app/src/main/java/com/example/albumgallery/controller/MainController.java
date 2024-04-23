package com.example.albumgallery.controller;

import android.app.Activity;

public class MainController {
    private final AlbumController albumController;
    private final ImageController imageController;
    private final ImageAlbumController imageAlbumController;
    private final UserController userController;

    public MainController(Activity activity) {
        albumController = new AlbumController(activity);
        imageController = new ImageController(activity);
        imageAlbumController = new ImageAlbumController(activity);
        userController = new UserController(activity);
    }

    public AlbumController getAlbumController() {
        return albumController;
    }

    public ImageController getImageController() {
        return imageController;
    }

    public ImageAlbumController getImageAlbumController() {
        return imageAlbumController;
    }

    public UserController getUserController() {
        return userController;
    }
}
