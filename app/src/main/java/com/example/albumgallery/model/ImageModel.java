package com.example.albumgallery.model;

public class ImageModel implements Model{
    private int id;
    private String name;
    private int id_size;
    private int capacity;
    private String created_at;
    private String notice;
    private String remain_time;
    private boolean is_deleted;
    private boolean is_favourited;

    public ImageModel(int id, String name, int id_size, int capacity){
        this.id = id;
        this.name = name;
        this.id_size = id_size;
        this.capacity = capacity;
        this.created_at = created_at;
        this.notice = notice;
        this.remain_time = remain_time;
        this.is_deleted = is_deleted;
        this.is_favourited = is_favourited;
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

    public int getId_size() {
        return id_size;
    }

    public void setId_size(int id_size) {
        this.id_size = id_size;
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

    public boolean isIs_favourited() {
        return is_favourited;
    }

    public void setIs_favourited(boolean is_favourited) {
        this.is_favourited = is_favourited;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", id_size=" + id_size +
                ", capacity=" + capacity +
                ", created_at='" + created_at + '\'' +
                ", notice='" + notice + '\'' +
                ", remain_time='" + remain_time + '\'' +
                ", is_deleted=" + is_deleted +
                ", is_favourited=" + is_favourited +
                '}';
    }

    @Override
    public String insert() {
        return "'" + id + "', '" + name + "', '" + id_size + "', '" + capacity + "', '" + created_at + "', '" + notice + "', '" + remain_time + "', '" + is_deleted + "', '" + is_favourited + "'";
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
