package com.example.albumgallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.example.albumgallery.view.HomeScreen;

public class MainActivity extends ComponentActivity {

    DatabaseManager db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.testBtn);
        final Button editButton = findViewById(R.id.editImageButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeScreen.class);
            startActivity(intent);
        });
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.albumgallery.view.LoginScreen.class);
            startActivity(intent);
        });

        try {
            db = new DatabaseManager(this);
            db.open();

        } catch (Exception e) {
            Log.d(e.getMessage(), "onCreate: ");
        }

        // set up
//        FirebaseManager firebaseManager = new FirebaseManage  r(this);
//        DatabaseManager databaseManager = new DatabaseManager(this);
    }
}