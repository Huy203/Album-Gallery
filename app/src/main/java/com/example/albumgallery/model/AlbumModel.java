package com.example.albumgallery.model;

import java.util.Calendar;

public class AlbumModel implements Model {
    // AlbumModel class is used to store the data of the album.
    private String thumbnail;
    private String id; // id of the album
    private String name; // name of the album
    private int capacity; // capacity of the album
    private String created_at; // created date of the album
    private String notice; // notice of the album
    private String ref;
    private String password;
    private int num_of_images;   // number of images
    private int is_deleted; // is album deleted

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

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getNum_of_images() {
        return num_of_images;
    }

    public int getIs_deleted() {
        return is_deleted;
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

    public AlbumModel(String name, String password, int num_of_images) {
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

    public AlbumModel() {
        this.name = "";
        this.capacity = 0;
        this.id = "";
        this.created_at = "";
        this.notice = "";
        this.ref = "";
        this.is_deleted = 0;
        this.num_of_images = 0;
        this.password = "";
        this.thumbnail = "";
    }

    public AlbumModel(String id, String name, int capacity, String created_at, String notice, String ref, String password, int num_of_images, int is_deleted, String thumbnail) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.created_at = created_at;
        this.notice = notice;
        this.ref = ref;
        this.password = password;
        this.num_of_images = num_of_images;
        this.is_deleted = is_deleted;
        this.thumbnail = thumbnail;
    }

    @Override
    public String insert() {
        return "INSERT INTO Album (id, name, capacity, created_at, notice, ref, is_deleted, num_of_images, password, thumbnail) " +
                "VALUES ('" + id + "', '" + name + "', " + capacity + ", '" + created_at + "', '" + notice + "', '" + ref + "', " + is_deleted + ", " + num_of_images + ", '" + password + "', '" + thumbnail + "')";
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
