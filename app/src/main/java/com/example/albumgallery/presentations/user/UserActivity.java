package com.example.albumgallery.presentations.user;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.albumgallery.R;
import com.example.albumgallery.helper.SharePreferenceHelper;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class UserActivity extends AppCompatActivity {
    private boolean isDarkMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        isDarkMode = SharePreferenceHelper.isDarkModeEnabled(this);
        SwitchMaterial switchMaterial = findViewById(R.id.darkModeSwitch);
        if(isDarkMode) {
            switchMaterial.setChecked(true);
        }
    }

    public void darkModeAction(View view){
        if(isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            SharePreferenceHelper.setDarkModeEnabled(this, false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            SharePreferenceHelper.setDarkModeEnabled(this, true);
        }
    }

    public void changeAvatarAction(View view){
        Log.d("UserActivity", "changeAvatarAction");
    }

    public void signOutAction(View view){
        Log.d("UserActivity", "signOutAction");
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
}
