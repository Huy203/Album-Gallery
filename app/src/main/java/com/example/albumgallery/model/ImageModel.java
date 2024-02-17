package com.example.albumgallery.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class ImageModel{
//    get all images from device
    public static List<String> getAllImages(Context context) {
        List<String> pathImg = new ArrayList<>();

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,null,null);
        if (cursor != null){
            while (cursor.moveToNext()){
                @SuppressLint("Range") String imagePath = cursor.getString
                        (cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                pathImg.add(imagePath);
            }
        }
        return pathImg;
    }
}
