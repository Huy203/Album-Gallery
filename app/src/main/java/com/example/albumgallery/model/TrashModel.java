package com.example.albumgallery.model;

import android.content.Context;

import com.example.albumgallery.DatabaseHelper;

public class TrashModel implements Model{
    private int id;
    private int capacity;
    private DatabaseHelper dbHelper;

    public TrashModel(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public TrashModel(int id, int capacity){
        this.id = id;
        this.capacity = capacity;
    }
    @Override
    public String insert() {
        return "INSERT INTO TrashModel (capacity) VALUES (" + capacity + ")";
    }

    @Override
    public void delete() {

    }

    @Override
    public void update() {

    }

    @Override
    public void select() {

    }
}
