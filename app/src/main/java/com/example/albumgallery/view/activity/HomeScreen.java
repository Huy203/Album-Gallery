package com.example.albumgallery.view.activity;

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

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.example.albumgallery.view.adapter.ImageAdapterListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeScreen extends AppCompatActivity implements BackgroundProcessingCallback, ImageAdapterListener {
    private static final int CAMERA_REQUEST_CODE = 100;
    private boolean isBackgroundTaskCompleted = true;
    private RecyclerView recyclerMediaView;
    private List<String> imageURIs; //contains the list of image encoded.
    private ImageAdapter imageAdapter; //adapter for the recycler view
    private MainController mainController; //controller contains other controllers
    TextView numberOfImagesSelected;

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

        Button btnPickImageFromDevice = findViewById(R.id.btnPickImageFromDevice);
        Button btnPickMultipleImages = findViewById(R.id.btnPickMultipleImages);

        numberOfImagesSelected = findViewById(R.id.numberOfSelectedImages);

        // Trong phương thức onCreate của HomeScreen.java
        TextView textAlbums = findViewById(R.id.textAlbums);
        textAlbums.setOnClickListener(view -> {
            // Chuyển sang màn hình mới
            Intent intent = new Intent(HomeScreen.this, AlbumActivity.class);
            startActivity(intent);
            finish();
        });

        btnPickImageFromDevice.setOnClickListener(view -> {
            isBackgroundTaskCompleted = false;
            mainController.getImageController().pickMultipleImages(this);
        });

        btnPickMultipleImages.setOnClickListener(view -> {
            if (imageAdapter.toggleMultipleChoiceImagesEnabled()) {
                btnPickMultipleImages.setText("Cancel");
                numberOfImagesSelected.setVisibility(TextView.VISIBLE);
                numberOfImagesSelected.setText("0 images selected");
            } else {
                btnPickMultipleImages.setText("Select");
                numberOfImagesSelected.setVisibility(TextView.GONE);
                imageAdapter.clearSelectedItems();
            }
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
//        imageURIs.addAll(mainController.getImageController().getAllImageURLs());
        // lấy ảnh sort theo date (mới nhất xếp trước).
        imageURIs.addAll(mainController.getImageController().getAllImageURLsSortByDate());
        imageAdapter = new ImageAdapter(this, imageURIs);
        recyclerMediaView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerMediaView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackgroundTaskCompleted() {
        isBackgroundTaskCompleted = true;
        updateUI();
        isBackgroundTaskCompleted = false;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void getSelectedItemsCount(int count) {
        Log.v("SelectedItems", count + " items selected");
        numberOfImagesSelected.setText(count + " images selected");
    }
}




