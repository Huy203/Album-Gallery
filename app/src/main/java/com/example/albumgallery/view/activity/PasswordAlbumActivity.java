package com.example.albumgallery.view.activity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.google.android.material.textfield.TextInputEditText;

public class PasswordAlbumActivity extends AppCompatActivity {
    private String albumName;
    private MainController mainController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_album_password);
        albumName = getIntent().getStringExtra("albumName");
        TextView albumNameTxtView = (TextView) findViewById(R.id.albumNameAlbumPassword);
        albumNameTxtView.setText(albumName);
        mainController = new MainController(this);
        TextInputEditText inputPasswordEditTxt = (TextInputEditText) findViewById(R.id.inputAlbumPassword);
        Button enterButton = (Button) findViewById(R.id.enterAlbumPasswordBtn);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPassword = inputPasswordEditTxt.getText().toString();
                String password = mainController.getAlbumController().getPasswordByAlbumName(albumName);
                if(password.equals(inputPassword)) {
                    Intent intent = new Intent(PasswordAlbumActivity.this, AlbumContentActivity.class);
                    intent.putExtra("albumName", albumName);
                    startActivity(intent);
                    finish();
                } else {
                    TextView passwordError = (TextView) findViewById(R.id.passwordError);
                    passwordError.setText("(*) Incorrect password");
                    passwordError.setVisibility(View.VISIBLE);
                }
            }
        });
        ImageButton backBtn = (ImageButton) findViewById(R.id.backButtonAlbumPassword);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainFragment = new Intent(PasswordAlbumActivity.this, MainFragmentController.class);
                mainFragment.putExtra("fragmentToLoad", "AlbumMain");
                startActivity(mainFragment);
                finish();
            }
        });
    }
}
