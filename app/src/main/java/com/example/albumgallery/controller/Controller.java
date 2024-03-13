package com.example.albumgallery.controller;

import com.example.albumgallery.model.Model;

public interface Controller {
    void create(String name, int width, int height, long capacity, String dateAdded);

    void insert(Model model); // Insert a model

    void update(String column, String value, String where); // Update a model

    void delete(String where); // Delete a model
}
