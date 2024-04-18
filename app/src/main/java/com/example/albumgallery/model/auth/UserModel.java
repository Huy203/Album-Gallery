package com.example.albumgallery.model.auth;

import android.util.Log;

import com.example.albumgallery.model.Model;

public class UserModel implements Model {
    // User class for authentication
    private String id;
    private String username;
    private String email;
    private String phone;
    private String created_at;
    private String birth;
    private String picture;

    public UserModel() {
    }

    public UserModel(String... args) {
        this.id = args[0];
        this.username = args[1];
        this.email = args[2];
        this.phone = args[3];
        this.created_at = args[4];
        this.birth = args[5];
        if(args.length > 6) {
            this.picture = args[6];
        }
        else{
            this.picture = "";
        }
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getBirth() {
        return birth;
    }

    public String getPicture() {
        return picture;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setPicture(String urlPicture) {
        this.picture = urlPicture;
    }

    public String insert() {
        Log.v("UserModel", "INSERT INTO User (id, username, email, phone, created_at, birth, picture) VALUES ('" + id + "', '" + username + "', '" + email + "', '" + phone + "', '" + created_at + "', '" + birth + "', '" + picture + "')");
        return "INSERT INTO User (id, username, email, phone, created_at, birth, picture) VALUES ('" + id + "', '" + username + "', '" + email + "', '" + phone + "', '" + created_at + "', '" + birth + "', '" + picture + "')";
    }

    public void delete() {
        // Delete the user
    }

    public void update() {
        // Update the user
    }

    @Override
    public void select() {

    }
}
