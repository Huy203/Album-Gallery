package com.example.albumgallery;

import android.app.Activity;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class FirebaseManager {

    private static FirebaseManager instance;
    private DatabaseReference databaseRef;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseStorage storage;
    private FirebaseAuth firebaseAuth;

    private FirebaseManager(Activity activity) {
        firebaseAuth = FirebaseAuth.getInstance();
        if (FirebaseApp.getApps(activity).isEmpty()) {
            FirebaseApp.initializeApp(activity);
        } else {
            FirebaseApp.getInstance();
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Log.v("Firebase", "User is signed in");
        } else {
            signInAnonymously();
        }

        Log.v("Firebase", "Firebase initialized");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity);
        databaseRef = FirebaseDatabase.getInstance().getReference("message");
        storage = FirebaseStorage.getInstance("gs://album-gallery-70d05.appspot.com");
    }

    public static synchronized FirebaseManager getInstance(Activity activity) {
        if (instance == null) {
            instance = new FirebaseManager(activity);
        }
        return instance;
    }

    public void getDatabaseRef() {
        databaseRef.child("message").setValue("Hello, World!");
    }

    public FirebaseStorage getStorage() {
        return storage;
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
}
