package com.example.albumgallery.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Utilities {
    public static String generateID() {
        return UUID.randomUUID().toString();
    }
    public static String presentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
