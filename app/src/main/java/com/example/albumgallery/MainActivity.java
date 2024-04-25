package com.example.albumgallery;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.albumgallery.helper.SharePreferenceHelper;
import com.example.albumgallery.view.activity.Auth;
import com.example.albumgallery.view.activity.MainFragmentController;

public class MainActivity extends ComponentActivity {
    DatabaseManager db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isDarkMode = SharePreferenceHelper.isDarkModeEnabled(this);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        db = new DatabaseManager(this);
        db.open();
        FirebaseManager firebaseManager = FirebaseManager.getInstance(this);
        firebaseManager.getFirebaseAuth().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                startActivity(new Intent(this, Auth.class));
                finish();
            } else {
                startActivity(new Intent(this, MainFragmentController.class));
                finish();
            }
        });
    }
}