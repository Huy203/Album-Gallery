package com.example.albumgallery.view.activity;

import static android.content.Intent.getIntent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.view.fragment.ImageInfo;
import com.example.albumgallery.view.listeners.OnSwipeTouchListener;
import com.bumptech.glide.Glide;

public class DetailDeletedPicture extends DetailPicture {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_deleted_image);

        mainController = new MainController(this);
        imagePaths = mainController.getImageController().getImagePaths();
        currentPosition = getIntent().getIntExtra("position", 0);

        imageView = findViewById(R.id.memeImageView);
        loadImage(currentPosition);

        // Detect swipe gestures
        imageView.setOnTouchListener(new OnSwipeTouchListener(this, imageView) {
            @Override
            public void onSwipeLeft() {
                if (currentPosition < imagePaths.size() - 1) {
                    currentPosition++;
                    loadImage(currentPosition);
                }
            }

            @Override
            public void onSwipeRight() {
                if (currentPosition > 0) {
                    currentPosition--;
                    loadImage(currentPosition);
                }
            }
        });
        ImageInfo imageInfoFragment = new ImageInfo();

        ImageView backButton = findViewById(R.id.backButton);
        ImageView trashButton = findViewById(R.id.trashButton);
        ImageView restoreButton = findViewById(R.id.restoreButton);
        Button ImageInfo = findViewById(R.id.ImageInfo);
        imageInfoView = findViewById(R.id.imageInfo);

        backButton.setOnClickListener(v -> {
            //Intent intent = new Intent(DetailPicture.this, HomeScreen.class);
//            startActivity(intent);
            supportFinishAfterTransition();
        });

        String uri = getImageModel().getRef();
        trashButton.setOnClickListener(v -> {
            if (uri != null) {
                // Call deleteSelectedImage() method from ImageController
                // mainController.getImageController().deleteSelectedImage(uri, 0);
                showDeleteConfirmationDialog(uri);
            } else {
                Toast.makeText(this, "No image to delete", Toast.LENGTH_SHORT).show();
            }
        });

        restoreButton.setOnClickListener(v -> {
            if (uri != null) {
                // Call deleteSelectedImage() method from ImageController
                // mainController.getImageController().deleteSelectedImage(uri, 0);
                showRestoreConfirmationDialog(uri);
            } else {
                Toast.makeText(this, "No image to restore", Toast.LENGTH_SHORT).show();
            }
        });

        //        // Lấy ảnh từ image adapter, hiển thị vào edit image screen.
//        String imagePath = getIntent().getStringExtra("imagePath");
//        ImageView imageView = findViewById(R.id.memeImageView);
//        Glide.with(this).load(Uri.parse(imagePath)).into(imageView);
        ImageInfo.setOnClickListener(v -> {
            toggleImageInfo();
        });

        imageInfoFragment.setImage(getImageModel());

        // Add ImageInfo fragment to activity
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .add(R.id.imageInfo, imageInfoFragment)
                .commit();


        ImageView imageView = findViewById(R.id.memeImageView);
        Glide.with(this).load(Uri.parse(uri)).into(imageView);
    }
    @Override
    protected void showDeleteConfirmationDialog(String uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this image forever?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call deleteSelectedImage() method from ImageController
                mainController.getImageController().deleteSelectedImage(uri);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    protected void showRestoreConfirmationDialog(String uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Restore");
        builder.setMessage("Restore this image?");
        builder.setPositiveButton("Restore", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call deleteSelectedImage() method from ImageController
                mainController.getImageController().restoreSelectedImage(uri);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
