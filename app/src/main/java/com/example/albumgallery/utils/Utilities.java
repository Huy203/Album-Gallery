package com.example.albumgallery.utils;

import java.util.UUID;

public class Utilities {
    public static String generateID() {
        return UUID.randomUUID().toString();
    }
}
