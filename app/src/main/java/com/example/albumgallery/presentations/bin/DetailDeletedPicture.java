package com.example.albumgallery.presentations.bin;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.view.activity.MainFragmentController;
import com.example.albumgallery.view.fragment.ImageInfo;
import com.example.albumgallery.view.listeners.ImageInfoListener;
import com.example.albumgallery.view.listeners.OnSwipeTouchListener;

import java.util.List;

public class DetailDeletedPicture extends AppCompatActivity implements ImageInfoListener {
    protected MainController mainController;
    protected ImageView imageView;
    protected List<String> imagePaths;
    protected int currentPosition;
    protected View imageInfoView;
    protected ImageInfo imageInfoFragment;
    protected boolean isImageInfoVisible = false;
    ImageModel imageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_deleted_image);

        initializeViews();
        setupListeners();
        loadImage(currentPosition);
        loadImageInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }

    private void update() {
        String id = getIntent().getStringExtra("id");
        imageModel = mainController.getImageController().getImageById(id);
    }

    private void initializeViews() {
        mainController = new MainController(this);
        imagePaths = mainController.getImageController().getAllImageURLsDeleted();
        currentPosition = getIntent().getIntExtra("position", 0);

        imageView = findViewById(R.id.memeImageView);

        imageInfoView = findViewById(R.id.imageInfo);

        imageInfoFragment = new ImageInfo();
        update();
    }

    @SuppressLint("NonConstantResourceId")
    private void setupListeners() {
        // Swipe gestures
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
    }

    protected void showDeleteConfirmationDialog(String uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this image forever?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call deleteSelectedImage() method from ImageController
                finish();
                // mainController.getImageController().deleteSelectedImage(uri);

                String id = mainController.getImageController().getIdByRef(uri);
                String userID = mainController.getImageController().getFirebaseManager().getFirebaseAuth().getCurrentUser().getUid();
                if (id != null) {
                    mainController.getImageController().getFirebaseManager().getFirebaseHelper().deleteImage("Image", id, userID);
                    mainController.getImageController().delete("id = '" + id + "'");
                }

                Intent intent = new Intent(DetailDeletedPicture.this, MainFragmentController.class);
                intent.putExtra("fragmentToLoad", "Bin");
                startActivity(intent);

            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    protected void showRestoreConfirmationDialog(String uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Restore");
        builder.setMessage("Are you sure you want to restore this image?");
        builder.setPositiveButton("Restore", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call deleteSelectedImage() method from ImageController
                finish();
                // mainController.getImageController().deleteSelectedImage(uri);

                String id = mainController.getImageController().getIdByRef(uri);
                if (id != null) {
                    mainController.getImageController().setDelete(id, false);
                    mainController.getImageController().delete("id = '" + id + "'");
                }

                Intent intent = new Intent(DetailDeletedPicture.this, MainFragmentController.class);
                intent.putExtra("fragmentToLoad", "Bin");
                startActivity(intent);

            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void loadImage(int currentPosition) {
        Glide.with(this).load(Uri.parse(imagePaths.get(currentPosition))).into(imageView);
    }

    private void loadImageInfo() {
        TextView nameImage = findViewById(R.id.nameTextView);
        TextView dateImage = findViewById(R.id.timeTextView);
        nameImage.setText(imageModel.getName());
        dateImage.setText(imageModel.getCreated_at());
        imageInfoFragment.setImage(imageModel);
        // Add ImageInfo fragment to activity
        getSupportFragmentManager().beginTransaction()
                .add(R.id.imageInfo, imageInfoFragment)
                .addToBackStack(null)
                .commit();
    }

    public void deleteAction(View view) {
        if (imageModel != null) {
            showDeleteConfirmationDialog(imageModel.getRef());
        } else {
            Toast.makeText(this, "No image to delete", Toast.LENGTH_SHORT).show();
        }
    }

    public void restoreAction(View view) {
        if (imageModel != null) {
            showRestoreConfirmationDialog(imageModel.getRef());
        } else {
            Toast.makeText(this, "No image to delete", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNoticePassed(String notice) {

    }

    public void backAction(View view) {
        Intent intent = new Intent();
        intent.putExtra("update", false);
        setResult(RESULT_OK, intent);
        supportFinishAfterTransition();
    }
}
