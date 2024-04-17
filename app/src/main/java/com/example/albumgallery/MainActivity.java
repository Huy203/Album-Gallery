package com.example.albumgallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.albumgallery.controller.MainController;
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
        Log.v("MainActivity", "isDarkMode: " + isDarkMode);
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
                Log.v("MainActivity", "User is not signed in");
                startActivity(new Intent(this, Auth.class));
                finish();
            } else {
                Log.v("MainActivity", "User is signed in");
                startActivity(new Intent(this, MainFragmentController.class));
                finish();
            }
        });


        // Step 1: Export Data from SQLite
//        List<String> list = db.getDbHelper().getAll("Image");
//        Log.v("SQLite", "List: " + list.size());
//        if (list != null) {
//            for (String item : list) {
//                Log.d("SQLite", item);
//            }
//        }
//        // Step 2: Convert Data to Firestore-Compatible Format
//        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
//        CollectionReference collectionReference = firestore.collection("Image");
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                Map<String, Object> data = new HashMap<>();
//                data.put("field1", cursor.getString(cursor.getColumnIndex("field1")));
//                data.put("field2", cursor.getInt(cursor.getColumnIndex("field2")));
//                // Add more fields as needed
//
//                // Step 3: Upload Data to Firestore
//                collectionReference.add(data)
//                        .addOnSuccessListener((OnSuccessListener<Void>) aVoid -> Log.d("Firestore", "Data added successfully"))
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.e("Firestore", "Error adding data: " + e.getMessage());
//                            }
//                        });
//            } while (cursor.moveToNext());
//            cursor.close();
//        }
//
//        sqLiteDatabase.close();
//
//    }
    }
}