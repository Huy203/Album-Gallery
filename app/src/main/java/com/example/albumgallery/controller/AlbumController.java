package com.example.albumgallery.controller;

import com.example.albumgallery.model.AlbumModel;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AlbumController {
    private AlbumModel album;

    public AlbumController(Context context) {
        album = new AlbumModel(context);
    }
    public void getAllAlbums() {
        // Get all albums

    }
    public void addAlbum() {
        // Add an album

    }

    public void deleteAlbum() {
        // Delete an album
    }

    public void editAlbum() {
        // Edit an album
    }

    public void viewAlbum() {
        // View an album
    }

    public void shareAlbum() {
        // Share an album
    }

    public void favouriteAlbum() {
        // Favourite an album
    }

    public void unfavouriteAlbum() {
        // Unfavourite an album
    }
}
