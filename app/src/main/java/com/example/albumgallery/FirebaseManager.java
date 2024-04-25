package com.example.albumgallery;

import android.app.Activity;
import android.util.Log;

import com.example.albumgallery.helper.FirebaseHelper;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseManager {

    private static FirebaseManager instance;
    private FirebaseDatabase database;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseStorage storage;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseHelper firebaseHelper;

    private FirebaseManager(Activity activity) {
        firebaseAuth = FirebaseAuth.getInstance();
        if (FirebaseApp.getApps(activity).isEmpty()) {
            FirebaseApp.initializeApp(activity);
        } else {
            FirebaseApp.getInstance();
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            signInUser(user);
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance("gs://album-gallery-70d05.appspot.com");
        firestore = FirebaseFirestore.getInstance();
        firebaseHelper = new FirebaseHelper();
    }

    public static synchronized FirebaseManager getInstance(Activity activity) {
        if (instance == null) {
            instance = new FirebaseManager(activity);
        }
        return instance;
    }

    public FirebaseStorage getStorage() {
        return storage;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public FirebaseHelper getFirebaseHelper() {
        return firebaseHelper;
    }

    private void signInAnonymously() {
        FirebaseAuth.getInstance().signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Firebase", "signInAnonymously:success");
                    } else {
                        Log.e("Firebase", "signInAnonymously:failure", task.getException());
                    }
                });
    }

    private void signInUser(FirebaseUser user) {
        Log.v("Firebase", "User: " + user.getEmail());
    }
}
