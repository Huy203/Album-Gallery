package com.example.albumgallery;

import android.Manifest;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import android.view.View;
import android.widget.Button;

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

        Button editImageButton = findViewById(R.id.editImageButton);
        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditImageActivity();
            }
        });
    }

    String test() {
        TextView textView = findViewById(R.id.textView);
        textView.setText("Hello World");
        return "Hello World";
    }

    private void openEditImageActivity() {
        Intent intent = new Intent(this, com.example.albumgallery.view.EditImageActivity.class);
        startActivity(intent);
    }
}
