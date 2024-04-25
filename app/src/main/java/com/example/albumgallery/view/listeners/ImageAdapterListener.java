package com.example.albumgallery.view.listeners;

import android.view.View;

public interface ImageAdapterListener {

    void handleImagePick(View itemView, String uri, int position);

    void getInteractedURIs(String uri);

    void toggleMultipleChoice();
}
