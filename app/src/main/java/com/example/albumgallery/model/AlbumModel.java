package com.example.albumgallery.model;

import android.content.Context;

import com.example.albumgallery.DatabaseHelper;

import java.util.Date;
import java.util.Calendar;
public class AlbumModel implements Model{
    // AlbumModel class is used to store the data of the album.
    private DatabaseHelper dbHelper;
    private int id; // id of the album
    private String name; // name of the album
    private int capacity; // capacity of the album
    private String created_at; // created date of the album
    private String notice; // notice of the album
    private String remain_time; // remaining time of the album
    private boolean is_deleted; // is album deleted
    private boolean is_favourited; // is album favourited

    public AlbumModel(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public AlbumModel(int id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.created_at = (Calendar.getInstance().getTime()).toString();
        this.notice = "";
        this.remain_time = "";
        this.is_deleted = false;
        this.is_favourited = false;
    }

    @Override
    public String insert() {
        return "INSERT INTO Album (name, capacity, created_at, notice, remain_time, is_deleted, is_favourited) VALUES ('" + name + ", " + capacity + ", '" + created_at + "', '" + notice + "', '" + remain_time + "', " + (is_deleted ? 1 : 0) + ", " + (is_favourited ? 1 : 0) + ")";
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
