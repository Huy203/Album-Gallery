package com.example.albumgallery.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.helper.SharePreferenceHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class CreateAlbumActivity extends AppCompatActivity {

    private MainController mainController;
    private TextView numOfImagesTextView;
    private MaterialButton backButton;
    private Button btnSelectImage;
    private TextInputLayout albumNameInputTxt;
    private SwitchMaterial passwordSwitch;
    private TextInputLayout passwordInputText;
    private Button btnCreateAlbum;
    private boolean isPrivate = false;
    private boolean isSelected = false;
    private List<String> selectedImageURLs;
    private List<Uri> selectedImageURIs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);

        // handle all the interactions
        handleInteractions();
        mainController = new MainController(this);
        selectedImageURLs = new ArrayList<>();
        selectedImageURIs = new ArrayList<>();

        // display number of images selected
        String numOfImages = getIntent().getStringExtra("numOfImages");
        numOfImagesTextView = (TextView) findViewById(R.id.numberOfSelectedImagesCreateAlbumActivity);
        numOfImagesTextView.setText(numOfImages);
        isSelected = getIntent().getBooleanExtra("isSelected", false);
        if (isSelected) {
            numOfImagesTextView.setVisibility(View.VISIBLE);
            for (String url : selectedImageURLs) {
                Log.d("create album activity", url);
            }
        }
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

        albumNameInputTxt = findViewById(R.id.albumName);

        passwordInputText = findViewById(R.id.passwordInputText);

        passwordSwitch = findViewById(R.id.passwordSwitch);
        passwordSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
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
                String numOfImagesString = getIntent().getStringExtra("numOfImages");
                Boolean isAlbumNameExists = mainController.getAlbumController().isAlbumNameExists(albumName);
                if (numOfImagesString == null) {
                    makeNotification(findViewById(R.id.relativeLayoutCreateAlbum), "Please choose at least 1 image");
                    return;
                }
                int numOfImages = numOfImagesString == null ? 0 : Integer.parseInt(String.valueOf(numOfImagesString.charAt(0)));
                if (albumName.isEmpty()) {
                    makeNotification(findViewById(R.id.relativeLayoutCreateAlbum), "Album's name is empty");
                    return;
                } else if (isAlbumNameExists) {
                    makeNotification(findViewById(R.id.relativeLayoutCreateAlbum), "Album's name already existed");
                    return;
                } else if (isPrivate && password.isEmpty()) {
                    makeNotification(findViewById(R.id.relativeLayoutCreateAlbum), "Album's password is empty");
                    return;
                } else if (numOfImages == 0) {
                    makeNotification(findViewById(R.id.relativeLayoutCreateAlbum), "Please choose at least 1 image");
                    return;
                }
                // add album
                mainController.getAlbumController().addAlbum(albumName, password, numOfImages);
                // get the album's id just added and add to album_image table
                String id_album = mainController.getAlbumController().getLastAlbumId();
                selectedImageURLs = getIntent().getStringArrayListExtra("selectedImageURIs");
                List<String> ids = new ArrayList<>();
                for (String uri : selectedImageURLs) {
                    ids.add(mainController.getImageController().getIdByRef(uri));
                }
                mainController.getAlbumController().updateThumbnailByAlbumName(albumName, selectedImageURLs.get(0));
                for (String id : ids) {
                    Log.d("Create Album Activity", id);
                    mainController.getImageAlbumController().addImageAlbum(id, id_album);
                }
                // navigate to album main after adding album
                Intent albumFrag = new Intent(CreateAlbumActivity.this, MainFragmentController.class);
                albumFrag.putExtra("fragmentToLoad", "AlbumMain");
                startActivity(albumFrag);
                finish();
            }
        });
    }

    private void makeNotification(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)
                .setTextColor(SharePreferenceHelper.isDarkModeEnabled(this) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black))
                .setBackgroundTint(SharePreferenceHelper.isDarkModeEnabled(this) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white))
                .show();

    }
}
