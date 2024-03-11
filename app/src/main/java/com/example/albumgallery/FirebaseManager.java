package com.example.albumgallery;

import androidx.activity.ComponentActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseManager {
    private DatabaseReference databaseRef;
    private FirebaseAnalytics mFirebaseAnalytics;

    private FirebaseStorage storage;

    public FirebaseManager(ComponentActivity activity) {
        FirebaseApp.initializeApp(activity);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
        databaseRef = FirebaseDatabase.getInstance().getReference("message");
        storage = FirebaseStorage.getInstance();
    }

    public void setValue(String value) {
        databaseRef.setValue(value);
    }
}