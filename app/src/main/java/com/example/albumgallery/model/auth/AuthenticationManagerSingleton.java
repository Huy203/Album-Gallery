package com.example.albumgallery.model.auth;

public class AuthenticationManagerSingleton {
    private static AuthenticationManager authManager;

    public static AuthenticationManager getInstance() {
        if (authManager == null) {
            authManager = new AuthenticationManager(null); // Thay bằng khởi tạo thích hợp của AuthenticationManager
        }
        return authManager;
    }
}

