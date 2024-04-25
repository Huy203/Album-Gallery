package com.example.albumgallery.model;

public class ImageModel implements Model {
    private String id;
    private String name;
    private int width;
    private int height;
    private long capacity;
    private String ref;
    private String created_at;
    private String notice;
    private String remain_time;
    private boolean is_deleted;
    private boolean is_favourited;

    public ImageModel(){
        this.id = "";
        this.name = "";
        this.width = 0;
        this.height = 0;
        this.capacity = 0;
        this.created_at = "";
        this.notice = "";
        this.ref = "";
        this.remain_time = "";
        this.is_deleted = false;
        this.is_favourited = false;}

    public ImageModel(String name, int width, int height, long capacity, String created_at) {
        this.id = "";
        this.name = name;
        this.width = width;
        this.height = height;
        this.capacity = capacity;
        this.created_at = created_at;
        this.notice = "";
        this.ref = "";
        this.remain_time = "";
        this.is_deleted = false;
        this.is_favourited = false;
    }

    public ImageModel(String id, String name, int width, int height, long capacity, String created_at, String notice, String ref, String remain_time, boolean is_deleted, boolean is_favourited) {
        this.id = id;
        this.name = name;
        this.width = width;
        this.height = height;
        this.capacity = capacity;
        this.created_at = created_at;
        this.notice = notice;
        this.ref = ref;
        this.remain_time = remain_time;
        this.is_deleted = is_deleted;
        this.is_favourited = is_favourited;
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
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

    public boolean getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public boolean getIs_favourited() {
        return is_favourited;
    }

    public void setIs_favourited(boolean is_favourited) {
        this.is_favourited = is_favourited;
    }


    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", capacity=" + capacity +
                ", ref='" + ref + '\'' +
                ", created_at='" + created_at + '\'' +
                ", notice='" + notice + '\'' +
                ", remain_time='" + remain_time + '\'' +
                ", is_deleted=" + is_deleted +
                ", is_favourited=" + is_favourited;
    }

    @Override
    public String insert() {
        return "INSERT INTO Image (id, name, width, height, capacity, ref, created_at, notice, remain_time, is_deleted, is_favourited) VALUES ('" + id + "','" + name + "', " + width + ", " + height + ", " + capacity + ", '" + ref + "', '" + created_at + "', '" + notice + "', '" + remain_time + "', " + (is_deleted ? 1 : 0) + ", " + (is_favourited ? 1 : 0) + ")";
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
