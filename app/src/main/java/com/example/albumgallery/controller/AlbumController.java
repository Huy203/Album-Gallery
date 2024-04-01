package com.example.albumgallery.controller;

import com.example.albumgallery.DatabaseHelper;
import com.example.albumgallery.model.AlbumModel;
import com.example.albumgallery.model.Model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class AlbumController implements Controller {
    private AlbumModel album;
    private final DatabaseHelper dbHelper;

    public AlbumController(Context context) {
        album = new AlbumModel(context);
        this.dbHelper = new DatabaseHelper(context);
    }
    private DatabaseHelper getDbHelper(){
        return dbHelper;
    }
    public List<AlbumModel> getAllAlbums() {
        // Get all albums
        List<String> data = dbHelper.getAll("Album");
        List<AlbumModel> albumModels = new ArrayList<>();
        for (String s : data) {
            String[] temp = s.split(",");
            albumModels.add(new AlbumModel(Integer.parseInt(temp[0]), temp[1], Integer.parseInt(temp[2]), temp[3], temp[4], Integer.parseInt(temp[5])));
        }
        return albumModels;
    }
    @Override
    public void insert(Model model) {
        dbHelper.insert("Album", model);
        dbHelper.close();
    }

    @Override
    public void update(String column, String value, String where) {
        dbHelper.update("Album", column, value, where);
        dbHelper.close();
    }

    @Override
    public void delete(String where) {
        dbHelper.delete("Album", where);
        dbHelper.close();
    }

    public void addAlbum(String name, String password, int numOfImages) {
        // Add an album
        AlbumModel albumModel = new AlbumModel(name, password, numOfImages);
        this.insert(albumModel);
    }

    public List<String> getAlbumNames() {
//        List<String> albumNames = dbHelper.select("Album","name", null);
        List<String> albumNames = dbHelper.getFromAlbum("name");
        return albumNames;
    }

    public long getLastAlbumId() {
        long res = dbHelper.getLastId("Album");
        return res;
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
