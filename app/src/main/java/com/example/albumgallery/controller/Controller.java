package com.example.albumgallery.controller;

import com.example.albumgallery.model.Model;

public interface Controller {
    void insert(Model model); // Insert a model

    void update(String column, String value, String where); // Update a model

    void delete(String where); // Delete a model
}
