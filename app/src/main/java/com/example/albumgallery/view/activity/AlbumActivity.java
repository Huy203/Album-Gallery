package com.example.albumgallery.view.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumgallery.R;
import com.example.albumgallery.view.adapter.ImageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlbumActivity extends AppCompatActivity {
    private RecyclerView recyclerMediaView;
    private List<String> imagePaths; //contains the list of image encoded.
    private ImageAdapter imageAdapter;
    private static final int REQUEST_CODE_PICK_MULTIPLE_IMAGES = 101;
    private static final int CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_home_screen);

        imagePaths = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imagePaths);
        recyclerMediaView = findViewById(R.id.recyclerMediaView);

        ImageButton btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(view -> openCamera());

        Button btnPickImage = findViewById(R.id.btnPickImage);

        btnPickImage.setOnClickListener(view -> pickMultipleImages());

        // Trong phương thức onCreate của HomeScreen.java
        TextView textAlbums = findViewById(R.id.textPhotos);
        textAlbums.setOnClickListener(view -> {
            // Chuyển sang màn hình mới
            Intent intent = new Intent(AlbumActivity.this, HomeScreen.class);
            startActivity(intent);
            finish();
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        super.onResume();
        imageAdapter = new ImageAdapter(this, imagePaths);
        recyclerMediaView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerMediaView.setAdapter(imageAdapter);
    }

    // function to pick multiple images from gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_MULTIPLE_IMAGES && resultCode == RESULT_OK && data != null) {
            handleImagePicked(data);
        }
    }

    private void handleImagePicked(Intent data) {
        if (data.getData() != null) {
            Uri uri = data.getData();
            imagePaths.add(uri.toString());
        } else if (data.getClipData() != null) {
            ClipData clipData = data.getClipData();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri();
                imagePaths.add(uri.toString());
            }
        }
    }

    private void pickMultipleImages() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, REQUEST_CODE_PICK_MULTIPLE_IMAGES);
    }
    // function to open camera on Emulator
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!= null) {
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
    }
}
