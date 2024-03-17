package com.example.albumgallery.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import com.example.albumgallery.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class RegisterScreen extends AppCompatActivity {

    private EditText eMails, passwordss, passwords01;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_register);



    }
}
