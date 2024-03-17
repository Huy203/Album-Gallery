package com.example.albumgallery.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.albumgallery.MainActivity;
import com.example.albumgallery.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginScreen extends AppCompatActivity {
    private EditText email, password;
    private FirebaseAuth mAuth;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);


        email = findViewById(R.id.eMail);
        password = findViewById(R.id.passwords);

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
//                startActivity(new Intent(LoginScreen.this, RegisterScreen.class));
                String strEmail = email.getText().toString();
                String strPassword = password.getText().toString();

                if (TextUtils.isEmpty(strEmail)){
                    Toast.makeText(LoginScreen.this, "Email không được để trống !", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(strPassword)) {
                    Toast.makeText(LoginScreen.this, "Password không được để trống !", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(strEmail,strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginScreen.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginScreen.this, MainActivity.class));
                        }
                        else {
                            Toast.makeText(LoginScreen.this, "Đăng nhập Thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}