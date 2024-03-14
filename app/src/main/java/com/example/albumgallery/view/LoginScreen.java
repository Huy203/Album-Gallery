package com.example.albumgallery.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.albumgallery.R;

import java.util.Objects;

public class LoginScreen extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);

        findViewById(R.id.singUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackground(getResources().getDrawable(R.drawable.switch_trcks, null));
                ((TextView) v).setTextColor(getResources().getColor(R.color.textColor, null));
                findViewById(R.id.logIn).setBackground(null);
                findViewById(R.id.singUpLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.logInLayout).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.logIn)).setTextColor(getResources().getColor(R.color.pinkColor, null));
            }
        });

        findViewById(R.id.logIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.singUp).setBackground(null);
                ((TextView) findViewById(R.id.singUp)).setTextColor(getResources().getColor(R.color.pinkColor, null));
                v.setBackground(getResources().getDrawable(R.drawable.switch_trcks, null));
                findViewById(R.id.singUpLayout).setVisibility(View.GONE);
                findViewById(R.id.logInLayout).setVisibility(View.VISIBLE);
                ((TextView) v).setTextColor(getResources().getColor(R.color.textColor, null));
            }
        });

        findViewById(R.id.singIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, RegisterScreen.class));
            }
        });
    }
}