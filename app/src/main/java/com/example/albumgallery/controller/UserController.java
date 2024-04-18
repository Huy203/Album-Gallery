package com.example.albumgallery.controller;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.helper.DatabaseHelper;
import com.example.albumgallery.model.Model;
import com.example.albumgallery.model.auth.UserModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

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
        firebaseManager.getFirebaseHelper().addDocument("User", model);
        dbHelper.insert("User", model);
    }

    @Override
    public void update(String column, String value, String where) {
        dbHelper.update("User", column, value, where);
        dbHelper.close();

        Map<String, Object> data = new HashMap<>();
        data.put(column, value);
        int start = where.indexOf("'") + 1;
        int end = where.lastIndexOf("'");
        String id = where.substring(start, end);
        Log.v("User", "Updating document with ID: " + id + " " + data.toString());
        firebaseManager.getFirebaseHelper().update("User", data, id, firebaseManager.getFirebaseAuth().getCurrentUser().getUid());
    }

    @Override
    public void delete(String where) {
        dbHelper.delete("User", where);
        dbHelper.close();
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

    public UserModel getUser() {
        String uid = firebaseManager.getFirebaseAuth().getCurrentUser().getUid();
        String data = dbHelper.getById("User", uid);
        Log.v("UserActivity", "Data: " + data);
        String[] temp = data.split(",");
        for (int i = 0; i < temp.length; i++) {
            Log.v("UserActivity", "Temp: " + temp[i]);
            if (temp[i].equals("null")) {
                temp[i] = "";
            }
        }
        return new UserModel(temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6]);
    }

    public void updateUser() {
        FirebaseUser user = firebaseManager.getFirebaseAuth().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Jane Q. User")
                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("UserActivity", "User profile updated.");
                        dbHelper.update("User", "username", "Jane Q. User", "id = '" + user.getUid() + "'");
                    }
                });
    }

    public List<String> getAllImageIds() {
        return dbHelper.getFromImage("id");
    }

    public void loadFromFirestore() {
        String uid = firebaseManager.getFirebaseAuth().getCurrentUser().getUid();
        String user = dbHelper.getById("User", uid);
        if (user == null) {
            firebaseManager.getFirebaseHelper().getById("User", uid, uid)
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot != null) {
                            Log.v("Image", "User added" + " " + documentSnapshot.getData());
                            String created_at = new Date().toString();
                            Log.v("Image", "Created at: " + created_at);
                            dbHelper.insert("User", new UserModel(
                                    documentSnapshot.get("id").toString(),
                                    documentSnapshot.get("username").toString(),
                                    documentSnapshot.get("email").toString(),
                                    documentSnapshot.get("phone").toString(),
                                    convertDateTime(documentSnapshot.get("created_at").toString()).toString(),
                                    documentSnapshot.get("birth").toString(),
                                    documentSnapshot.get("picture").toString()));
                        }
                    });
        }
    }

    public String convertDateTime(String datetime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy");
        inputFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // Set input timezone

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        outputFormat.setTimeZone(TimeZone.getTimeZone("GMT+7")); // Set output timezone

        try {
            Date date = inputFormat.parse(datetime);
            String outputDate = outputFormat.format(date);
            System.out.println("Converted date: " + outputDate);
            return outputDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datetime;
    }
}
