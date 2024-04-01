package com.example.albumgallery.controller;

import android.content.Context;

import com.example.albumgallery.DatabaseHelper;
import com.example.albumgallery.model.AlbumModel;
import com.example.albumgallery.model.Model;
import com.example.albumgallery.model.TrashModel;

public class TrashController implements Controller {
    private TrashModel trash;
    private final DatabaseHelper dbHelper;

    public TrashController(Context context) {
        trash = new TrashModel(context);
        this.dbHelper = new DatabaseHelper(context);
    }

    private DatabaseHelper getDbHelper(){
        return dbHelper;
    }
    @Override
    public void insert(Model model) {
        dbHelper.insert("Trash", model);
        dbHelper.close();
    }

    @Override
    public void update(String column, String value, String where) {
        dbHelper.update("Trash", column, value, where);
        dbHelper.close();
    }

    @Override
    public void delete(String where) {

    }
}
