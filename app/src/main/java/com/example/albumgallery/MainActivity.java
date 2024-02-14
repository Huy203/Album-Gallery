package com.example.albumgallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.albumgallery.view.AlbumActivity;

//import compose
public class MainActivity extends ComponentActivity {
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private FirebaseManager firebaseManager;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up Firebase
        firebaseManager = new FirebaseManager(this);

        // Start Album Activity
        firebaseManager.setValue(test());
    }
    String test(){
        TextView textView = findViewById(R.id.textView);
        textView.setText("Hello World");
        return "Hello World";
    }
}
