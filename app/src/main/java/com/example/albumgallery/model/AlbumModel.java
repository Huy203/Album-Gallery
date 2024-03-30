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
    private int num_of_images;   // number of images

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
    public String getRemain_time() {
        return remain_time;
    }

    public void setRemain_time(String remain_time) {
        this.remain_time = remain_time;
    }

    public boolean isIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

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

    public AlbumModel(int id, String name, int capacity, String ref, String password, int num_of_images) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.ref = ref;
        this.password = password;
        this.created_at = (Calendar.getInstance().getTime()).toString();
        this.is_deleted = false;
        this.password = "";
        this.notice = "";
        this.num_of_images = num_of_images;
    }

    @Override
    public String insert() {
        return "INSERT INTO Album (name, capacity, created_at, notice, remain_time, is_deleted, password) VALUES ('" + name + ", " + capacity + ", '" + created_at + "', '" + notice + "', '" + remain_time + "', " + (is_deleted ? 1 : 0) + ", " + password + "', " + ")";
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
