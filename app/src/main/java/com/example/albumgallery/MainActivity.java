package com.example.albumgallery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import com.example.albumgallery.view.HomeScreen;

//import compose
public class MainActivity extends ComponentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = findViewById(R.id.testbtn);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeScreen.class);
            startActivity(intent);
        });

        // set up
        FirebaseManager firebaseManager = new FirebaseManager(this);
        DatabaseManager databaseManager = new DatabaseManager(this);
    }
}
