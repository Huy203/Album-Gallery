package com.example.albumgallery.view.adapter;

import android.view.View;

import java.util.List;

public interface ImageAdapterListener {

    void getSelectedItemsCount(int count);
    void handleImagePick(View itemView, String uri, int position);
    void getInteractedURIs(String uri);
}
