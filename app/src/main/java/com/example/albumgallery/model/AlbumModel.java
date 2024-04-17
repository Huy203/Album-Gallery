package com.example.albumgallery.model;

import android.content.Context;

import com.example.albumgallery.helper.DatabaseHelper;

import java.util.Date;
import java.util.Calendar;
import java.util.List;

public class AlbumModel implements Model{
    // AlbumModel class is used to store the data of the album.
    private DatabaseHelper dbHelper;
    private String id; // id of the album
    private String name; // name of the album
    private int capacity; // capacity of the album
    private String created_at; // created date of the album
    private String notice; // notice of the album
    private String ref;
    private String password;
    private int num_of_images;   // number of images
    private int is_deleted; // is album deleted
    private String thumbnail;

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getNum_of_images() {
        return num_of_images;
    }

    public void setNum_of_images(int num_of_images) {
        this.num_of_images = num_of_images;
    }

    public int isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(int is_deleted) {
        this.is_deleted = is_deleted;
    }

    public AlbumModel(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public AlbumModel(String id, String name, int capacity, String notice, String ref, int num_of_images) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.ref = ref;
        this.created_at = (Calendar.getInstance().getTime()).toString();
        this.is_deleted = 0;
        this.notice = notice;
        this.num_of_images = num_of_images;
        this.thumbnail = "";
    }
    public AlbumModel(String name, String password, int num_of_images){
        this.name = name;
        this.capacity = 0;
        this.password = password;
        this.created_at = (Calendar.getInstance().getTime()).toString();
        this.notice = "";
        this.ref = "";
        this.is_deleted = 0;
        this.num_of_images = num_of_images;
        this.thumbnail = "";
    }

    @Override
    public String insert() {
        return "INSERT INTO Album (name, capacity, created_at, notice, ref, is_deleted, num_of_images, password) " +
                "VALUES ('" + name + "', " + capacity + ", '" + created_at + "', '" + notice + "', '" + ref + "', " + is_deleted + ", " + num_of_images + ", '" + password + "')";
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
