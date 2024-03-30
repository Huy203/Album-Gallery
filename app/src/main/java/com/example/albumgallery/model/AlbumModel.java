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
    private String ref;
    private String password;
    private String remain_time; // remaining time of the album
    private boolean is_deleted; // is album deleted

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
    }

    public AlbumModel(int id, String name, int capacity, String ref, String password) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.ref = ref;
        this.password = password;
        this.created_at = (Calendar.getInstance().getTime()).toString();
        this.is_deleted = false;
        this.password = "";
        this.notice = "";
    }

    @Override
    public String insert() {
        return null;
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
