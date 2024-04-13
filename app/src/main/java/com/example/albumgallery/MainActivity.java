package com.example.albumgallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.albumgallery.helper.SharePreferenceHelper;
import com.example.albumgallery.view.activity.LoginScreen;
import com.example.albumgallery.view.activity.MainFragmentController;

public class MainActivity extends ComponentActivity {
    DatabaseManager db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isDarkMode = SharePreferenceHelper.isDarkModeEnabled(this);
        Log.v("MainActivity", "isDarkMode: " + isDarkMode);
        if(isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        final Button button = findViewById(R.id.testBtn);
        final Button editButton = findViewById(R.id.editImageButton);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainFragmentController.class);
            startActivity(intent);
        });
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginScreen.class);
            startActivity(intent);
        });

        try {
            db = new DatabaseManager(this);
            db.open();

        } catch (Exception e) {
            Log.d(e.getMessage(), "onCreate: ");
        }
    }
}