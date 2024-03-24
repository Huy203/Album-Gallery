package com.example.albumgallery.view.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.example.albumgallery.view.adapter.ImageAdapterListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeScreen extends AppCompatActivity implements BackgroundProcessingCallback, ImageAdapterListener {
    private static final int CAMERA_REQUEST_CODE = 1000;
    private boolean isBackgroundTaskCompleted = true;
    private int haveTask = 0;
    private RecyclerView recyclerMediaView;
    private List<String> imageURIs; //contains the list of image encoded.
    private ImageAdapter imageAdapter; //adapter for the recycler view
    private MainController mainController; //controller contains other controllers
    TextView numberOfImagesSelected;
    List<String> selectedImageURLs;
    List<Task> selectedImageURLsTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_home_screen);

        mainController = new MainController(this);
        // Fetch images from DB
        imageURIs = new ArrayList<>();
        selectedImageURLs = new ArrayList<>();
        selectedImageURLsTask = new ArrayList<>();

        imageAdapter = new ImageAdapter(this, imageURIs);
        recyclerMediaView = findViewById(R.id.recyclerMediaView);

        ImageButton btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(view -> openCamera());

        Button btnPickImageFromDevice = findViewById(R.id.btnPickImageFromDevice);
        Button btnPickMultipleImages = findViewById(R.id.btnPickMultipleImages);
        Button btnDeleteMultipleImages = findViewById(R.id.btnDeleteMultipleImages);

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

        btnDeleteMultipleImages.setOnClickListener(view -> {
            // mainController.getImageController().deleteSelectedImageAtHomeScreeen(selectedImageURLsTask, 1);
            showDeleteConfirmationDialog();
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
        updateUI();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() == null) {
                Log.d("Check data", "is null");
            } else {
                Log.d("Check data", "is not null");
            }
        }
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

//        List<Task<Uri>> task =  new ArrayList<>();
//        for (String uri : imageURIs) {
//            task.add(Tasks.forResult(Uri.parse(uri)));
//        }

        for (int i = 0; i < count; i++) {
            selectedImageURLsTask.add(Tasks.forResult(Uri.parse(imageURIs.get(i))));
            Log.d("Deleted images task", selectedImageURLsTask.get(i).getResult().toString());
        }

        for (int i = 0; i < count; i++) {
            selectedImageURLs.add(imageURIs.get(i));
            Log.d("Deleted images", selectedImageURLs.get(i));
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this image?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call deleteSelectedImage() method from ImageController
                mainController.getImageController().deleteSelectedImageAtHomeScreeen(selectedImageURLsTask);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
