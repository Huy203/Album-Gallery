package com.example.albumgallery.view.adapter;

import android.view.View;

public interface ImageAdapterListener {

    void getSelectedItemsCount(int count);
    void handleImagePick(View itemView, String uri, int position);
}
