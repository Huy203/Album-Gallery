package com.example.albumgallery;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import compose
public class MainActivity extends ComponentActivity {
    private DatabaseReference databaseRef;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static final String TAG = "MainActivity";

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        databaseRef = FirebaseDatabase.getInstance().getReference("message");
        databaseRef.setValue(test());
    }

    String test(){
        TextView textView = findViewById(R.id.textView);
        textView.setText("Hello World");
        return "Hello World";
    }
}
