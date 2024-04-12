package com.example.albumgallery.view.listeners;

import android.view.View;

import java.util.List;

public interface ImageAdapterListener {

    void getSelectedItemsCount();
    void handleImagePick(View itemView, String uri, int position);
    void getInteractedURIs(String uri);
    void toggleMultipleChoice();
}
