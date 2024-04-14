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

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.helper.SharePreferenceHelper;
import com.example.albumgallery.model.auth.AuthenticationManager;
import com.example.albumgallery.model.auth.AuthenticationManagerSingleton;
import com.example.albumgallery.model.auth.UserModel;
import com.example.albumgallery.view.activity.LoginScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserActivity extends AppCompatActivity {
    private boolean isDarkMode;
    private MainController mainController;
    private AuthenticationManager authManager = AuthenticationManagerSingleton.getInstance();

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initializeVariables();

        SwitchMaterial switchMaterial = findViewById(R.id.darkModeSwitch);
        if (isDarkMode) {
            switchMaterial.setChecked(true);
        }
//
//        ((MaterialTextView)findViewById(R.id.userName)).setText(getUser().getUsername());
//        ((MaterialTextView)findViewById(R.id.emailTextView)).setText(getUser().getEmail());
//        ((MaterialTextView)findViewById(R.id.dateOfBirthTextView)).setText(getUser().getPhone());
//        ((MaterialTextView)findViewById(R.id.languageTextView)).setText(getUser().getAddress());

        if (user != null) {
            // User is signed in
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

            Log.v("UserActivity", "User is signed in" + user.getDisplayName() + " " + user.getEmail() + " " + user.getPhotoUrl() + " " + user.isEmailVerified() + " " + user.getUid());
            Log.v("UserActivity", "User is signed in" + user.getDisplayName());
        } else {
            // No user is signed in
            Log.v("UserActivity", "No user is signed in");
        }
    }

    private void initializeVariables() {
        mainController = new MainController(this);
        isDarkMode = SharePreferenceHelper.isDarkModeEnabled(this);
        user = mainController.getUserController().getFirebaseManager().getFirebaseAuth().getCurrentUser();
    }

    public void updateUser(){
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
        return mainController.getUserController().getUserById(1);
    }

    public void editAction(View view) {
        Log.d("UserActivity", "changeAvatarAction");
    }

    public void backAction(View view) {
        finish();
    }

    public void signOutAction(View view) {
        // Thực hiện đăng xuất
        if (authManager != null) {
            authManager.signOut(new AuthenticationManager.OnLogoutListener() {
                @Override
                public void onSuccess() {
                    // Đăng xuất thành công, thực hiện các hành động cần thiết (ví dụ: chuyển hướng đến màn hình đăng nhập)
                    Intent intent = new Intent(UserActivity.this, LoginScreen.class);
                    startActivity(intent);
                    finish(); // Đóng màn hình hiện tại (HomeScreenFragment)
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Xử lý khi đăng xuất thất bại (nếu cần)
                    Toast.makeText(UserActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}


//    @Override
//    public void showUsers(List<User> users) {
//        userAdapter = new UserAdapter(users);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(userAdapter);
//    }
//
//    @Override
//    public void showLoading() {
//        progressBar.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void hideLoading() {
//        progressBar.setVisibility(View.GONE);
//    }
