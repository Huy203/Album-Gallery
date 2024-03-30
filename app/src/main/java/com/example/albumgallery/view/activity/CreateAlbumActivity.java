package com.example.albumgallery.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class CreateAlbumActivity extends AppCompatActivity {

    private MainController mainController;
    private TextView numOfImagesTextView;
    private ImageButton backButton;
    private Button btnSelectImage;
    private TextInputLayout albumNameInputTxt;
    private MaterialSwitch passwordSwitch;
    private TextInputLayout passwordInputText;
    private Button btnCreateAlbum;
    private boolean isPrivate = false;
    private boolean isSelected = false;
    private List<String> selectedImageURLs;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);

        // handle all the interactions
        handleInteractions();
        mainController = new MainController(this);
        // display number of images selected
        String numOfImages = getIntent().getStringExtra("numOfImages");
        numOfImagesTextView = (TextView) findViewById(R.id.numberOfSelectedImagesCreateAlbumActivity);
        numOfImagesTextView.setText(numOfImages);
        isSelected = getIntent().getBooleanExtra("isSelected", false);
        if(isSelected) {
            numOfImagesTextView.setVisibility(View.VISIBLE);
        }
        selectedImageURLs = new ArrayList<>();
        selectedImageURLs = getIntent().getStringArrayListExtra("selectedImageURLs");


    }

    private void handleInteractions() {
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainFragment = new Intent(CreateAlbumActivity.this, MainFragmentController.class);
                mainFragment.putExtra("fragmentToLoad", "AlbumMain");
                startActivity(mainFragment);
                finish();
            }
        });

        btnSelectImage = (Button) findViewById(R.id.buttonChooseImage);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectImageActivity = new Intent(CreateAlbumActivity.this, SelectImageActivity.class);
                startActivity(selectImageActivity);
                finish();
            }
        });

        albumNameInputTxt = (TextInputLayout) findViewById(R.id.albumName);

        passwordInputText = (TextInputLayout) findViewById(R.id.passwordInputText);

        passwordSwitch = (MaterialSwitch) findViewById(R.id.passwordSwitch);
        passwordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked) {
                    passwordInputText.setVisibility(View.VISIBLE);
                    isPrivate = true;
                } else {
                    passwordInputText.setVisibility(View.GONE);
                    isPrivate = false;
                }
            }
        });

        btnCreateAlbum = (Button) findViewById(R.id.btnCreateAlbumActivity);
            btnCreateAlbum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String albumName = albumNameInputTxt.getEditText().getText().toString();
                    String password = passwordInputText.getEditText().getText().toString();
                    if(albumName.isEmpty()) {
                        makeNotification(findViewById(R.id.relativeLayoutCreateAlbum),"Album's name is empty");
                        return;
                    } else if (isPrivate && password.isEmpty()) {
                        makeNotification(findViewById(R.id.relativeLayoutCreateAlbum), "Album's password is empty");
                        return;
                    }
                    // add album
                    mainController.getAlbumController().addAlbum(albumName, password);
                }
            });
    }
    private void makeNotification(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
    }
}
