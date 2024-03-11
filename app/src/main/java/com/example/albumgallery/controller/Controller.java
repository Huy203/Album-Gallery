package com.example.albumgallery.controller;

import android.content.Context;

import com.example.albumgallery.DatabaseHelper;
import com.example.albumgallery.model.Model;

public class Controller {
    private DatabaseHelper dbHelper;

    public Controller(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void insert (String table, Model model) {
        // Insert data
        dbHelper.insert(table, model);
    }
}
