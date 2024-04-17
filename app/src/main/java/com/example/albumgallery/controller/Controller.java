package com.example.albumgallery.controller;

import android.app.Activity;

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.model.Model;

import java.util.List;

public interface Controller {
    void insert(Model model); // Insert a model

    void update(String column, String value, String where); // Update a model

    void delete(String where); // Delete a model
}
