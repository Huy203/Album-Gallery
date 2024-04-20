package com.example.albumgallery.view.listeners;

import android.graphics.Bitmap;
import android.graphics.Rect;

import java.util.List;

public interface TextRecognitionListener {
    void onTextRecognized(List<String> textRecognized, List<Rect> boundingBoxes, Bitmap bitmap);
}
