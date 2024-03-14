package com.example.albumgallery.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumgallery.BackgroundProcessingCallback;
import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.view.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeScreen extends AppCompatActivity implements BackgroundProcessingCallback {
    private static final int CAMERA_REQUEST_CODE = 100;

    private boolean isBackgroundTaskCompleted = true;
    private RecyclerView recyclerMediaView;
    private List<String> imageURIs; //contains the list of image encoded.
    private ImageAdapter imageAdapter; //adapter for the recycler view
    private MainController mainController; //controller contains other controllers
    private FirebaseManager firebaseManager; //firebase manager to handle firebase operations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_home_screen);

        mainController = new MainController(this);

        // Fetch images from DB
        imageURIs = new ArrayList<>();

        imageAdapter = new ImageAdapter(this, imageURIs);
        recyclerMediaView = findViewById(R.id.recyclerMediaView);

        ImageButton btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(view -> openCamera());

        Button btnPickImage = findViewById(R.id.btnPickImage);

        // Trong phương thức onCreate của HomeScreen.java
        TextView textAlbums = findViewById(R.id.textAlbums);
        textAlbums.setOnClickListener(view -> {
            // Chuyển sang màn hình mới
            Intent intent = new Intent(HomeScreen.this, AlbumActivity.class);
            startActivity(intent);
            finish();
        });

        btnPickImage.setOnClickListener(view -> {
            isBackgroundTaskCompleted = false;
            mainController.getImageController().pickMultipleImages(this);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        super.onResume();
        if (isBackgroundTaskCompleted)
            updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mainController.getImageController().onActivityResult(requestCode, resultCode, data);
    }

    // function to open camera on Emulator
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }

    private void updateUI() {
        imageURIs.clear();
        imageURIs.addAll(mainController.getImageController().getAllImageURLs());
        Log.v("ImageURIs", mainController.getImageController().getAllImageURLs().toString());

        imageAdapter = new ImageAdapter(this, imageURIs);
        recyclerMediaView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerMediaView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackgroundTaskCompleted() {
        Log.v("HomeScreen", "onBackgroundTaskCompleted");
        isBackgroundTaskCompleted = true;
        updateUI();
    }
}




