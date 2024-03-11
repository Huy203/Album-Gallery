package com.example.albumgallery.view;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class HomeScreen extends AppCompatActivity {
    private RecyclerView recyclerMediaView;
    private List<String> imagePaths; //contains the list of image encoded.
    private ImageAdapter imageAdapter;
    private static final int REQUEST_CODE_PICK_MULTIPLE_IMAGES = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_home_screen);

        imagePaths = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imagePaths);
        recyclerMediaView = findViewById(R.id.recyclerMediaView);

        ImageButton btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Button btnPickImage = findViewById(R.id.btnPickImage);

        btnPickImage.setOnClickListener(view -> pickMultipleImages());

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
}



