package com.example.albumgallery.controller;

import android.app.Activity;

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.helper.DatabaseHelper;
import com.example.albumgallery.model.Model;
import com.example.albumgallery.model.auth.UserModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
        firebaseManager.getFirebaseHelper().update("User", data, id, firebaseManager.getFirebaseAuth().getCurrentUser().getUid());
    }

    @Override
    public void delete(String where) {
        dbHelper.delete("User", where);
        dbHelper.close();
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
        String[] temp = data.split(",");
        for (int i = 0; i < temp.length; i++) {
            if (temp[i].equals("null")) {
                temp[i] = "";
            }
        }
        return new UserModel(temp[0], temp[1], temp[2], temp[3], temp[4], temp[5], temp[6]);
    }

    public void loadFromFirestore() {
        String uid = firebaseManager.getFirebaseAuth().getCurrentUser().getUid();
        String user = dbHelper.getById("User", uid);
        if (user == null) {
            firebaseManager.getFirebaseHelper().getById("User", uid, uid)
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot != null) {
                            dbHelper.insert("User", new UserModel(
                                    documentSnapshot.get("id").toString(),
                                    documentSnapshot.get("username").toString(),
                                    documentSnapshot.get("email").toString(),
                                    documentSnapshot.get("phone").toString(),
                                    convertDateTime(documentSnapshot.get("created_at").toString()),
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
            return outputDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return datetime;
    }
}
