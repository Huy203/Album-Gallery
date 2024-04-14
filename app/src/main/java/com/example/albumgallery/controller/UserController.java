package com.example.albumgallery.controller;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.helper.DatabaseHelper;
import com.example.albumgallery.model.Model;
import com.example.albumgallery.model.auth.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserController implements Controller {
    private final Activity activity;
    private final DatabaseHelper dbHelper;
    private final FirebaseManager firebaseManager;

    public UserController(Activity activity) {
        this.activity = activity;
        dbHelper = new DatabaseHelper(activity);
        firebaseManager = FirebaseManager.getInstance(activity);
    }

    @Override
    public void insert(Model model) {

    }

    @Override
    public void update(String column, String value, String where) {

    }

    @Override
    public void delete(String where) {

    }

    private DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public Activity getActivity() {
        return activity;
    }

    public FirebaseManager getFirebaseManager() {
        return firebaseManager;
    }

    public UserModel getUserById(long id) {
        String data = dbHelper.getById("User", id);
        String[] temp = data.split(",");
        Log.v("UserController", "getUserById: " + data);
        return new UserModel(temp[1], temp[2], temp[3], temp[4], temp[5]);
    }

    public void updateUser(){
        FirebaseUser user = firebaseManager.getFirebaseAuth().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Jane Q. User")
                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("UserActivity", "User profile updated.");
                        }
                    }
                });
    }
}
