package com.example.albumgallery.presentations.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.albumgallery.R;
import com.example.albumgallery.helper.SharePreferenceHelper;
import com.example.albumgallery.model.auth.AuthenticationManager;
import com.example.albumgallery.model.auth.AuthenticationManagerSingleton;
import com.example.albumgallery.view.activity.LoginScreen;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class UserActivity extends AppCompatActivity {
    private boolean isDarkMode;
    private AuthenticationManager authManager = AuthenticationManagerSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        isDarkMode = SharePreferenceHelper.isDarkModeEnabled(this);
        SwitchMaterial switchMaterial = findViewById(R.id.darkModeSwitch);
        if (isDarkMode) {
            switchMaterial.setChecked(true);
        }
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

    public void changeAvatarAction(View view) {
        Log.d("UserActivity", "changeAvatarAction");
    }

    public void backAction(View view){
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
