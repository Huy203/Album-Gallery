package com.example.albumgallery;

import androidx.activity.ComponentActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {
    private DatabaseReference databaseRef;
    private FirebaseAnalytics mFirebaseAnalytics;

    public FirebaseManager(ComponentActivity activity) {
        FirebaseApp.initializeApp(activity);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
        databaseRef = FirebaseDatabase.getInstance().getReference("message");
    }

    public void setValue(String value) {
        databaseRef.setValue(value);
    }
}