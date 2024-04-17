package com.example.albumgallery.presentations.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.UserController;
import com.example.albumgallery.helper.SharePreferenceHelper;
import com.example.albumgallery.model.auth.UserModel;
import com.example.albumgallery.view.activity.Auth;
import com.example.albumgallery.view.listeners.FragToActivityListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserActivity extends AppCompatActivity implements FragToActivityListener {
    private boolean isDarkMode;
    private UserController userController;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        userController = new UserController(this);
        isDarkMode = SharePreferenceHelper.isDarkModeEnabled(this);
        darkMode();

        userModel = getUser();
        initializeVariables();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }

    public void darkMode() {
        SwitchMaterial switchMaterial = findViewById(R.id.darkModeSwitch);
        if (isDarkMode) {
            switchMaterial.setChecked(true);
        }
    }

//        ((MaterialTextView)findViewById(R.id.userName)).setText(getUser().getUsername());
//        ((MaterialTextView)findViewById(R.id.emailTextView)).setText(getUser().getEmail());
//        ((MaterialTextView)findViewById(R.id.dateOfBirthTextView)).setText(getUser().getPhone());
//        ((MaterialTextView)findViewById(R.id.languageTextView)).setText(getUser().getAddress());

    private void initializeVariables() {
        if (userModel == null) {
            Intent intent = new Intent(UserActivity.this, Auth.class);
            startActivity(intent);
            finish();
        } else {
            Log.v("UserActivity", "UserModel: " + userModel.getUsername());
            MaterialTextView userName = findViewById(R.id.usernameTextView);
            userName.setText(userModel.getUsername());

            MaterialTextView emailTextView = findViewById(R.id.emailTextView);
            emailTextView.setText(userModel.getEmail());

            MaterialTextView dateOfBirthTextView = findViewById(R.id.dateOfBirthTextView);
            dateOfBirthTextView.setText(userModel.getBirth());

            MaterialTextView phoneTextView = findViewById(R.id.phoneTextView);
            phoneTextView.setText(userModel.getPhone());

//            ((MaterialTextView)findViewById(R.id.usernameTextView)).setText(userModel.getUsername());
//            ((MaterialTextView)findViewById(R.id.emailTextView)).setText(userModel.getEmail());
//            ((MaterialTextView)findViewById(R.id.dateOfBirthTextView)).setText(userModel.getBirth());
//            ((MaterialTextView)findViewById(R.id.phoneTextView)).setText(userModel.getPhone());
        }
        Log.v("UserActivity", "isDarkMode: " + isDarkMode);
    }

    public void updateUser() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("Jane Q. User")
                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        userController.getFirebaseManager().getFirebaseAuth().getCurrentUser().updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("UserActivity", "User profile updated.");
                        }
                    }
                });
    }

    public void darkModeAction(View view) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            SharePreferenceHelper.setDarkModeEnabled(this, false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            SharePreferenceHelper.setDarkModeEnabled(this, true);
        }
    }

    public UserModel getUser() {
        return userController.getUser();
    }

    public void editAction(View view) {
        DialogFragment dialogFragment = new UserEditFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, dialogFragment)
                .addToBackStack(null).commit();
    }

    public void backAction(View view) {
        finish();
    }

    public void signOutAction(View view) {
        userController.getFirebaseManager().getFirebaseAuth().signOut();
//        Intent intent = new Intent(UserActivity.this, Auth.class);
//        startActivity(intent);
//        finish();
    }

    @Override
    public void onFragmentAction(String action, Object data) {
        if (action.equals("update")) {
            updateUI();
        }
    }

    private void updateUI() {
        userModel = getUser();
        initializeVariables();
    }
}