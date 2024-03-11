package com.example.albumgallery.view;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeScreen extends AppCompatActivity {
    private RecyclerView recyclerMediaView;
    private List<String> imagePaths; //contains the list of image encoded.
    private ImageAdapter imageAdapter;
    private static final int REQUEST_CODE_PICK_MULTIPLE_IMAGES = 101;
    private static final int CAMERA_REQUEST_CODE = 100;
    private MainController mainController;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_home_screen);

        mainController = new MainController(this);

        imagePaths = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imagePaths);
        recyclerMediaView = findViewById(R.id.recyclerMediaView);

        ImageButton btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(view -> openCamera());

        Button btnPickImage = findViewById(R.id.btnPickImage);

        btnPickImage.setOnClickListener(view -> {
            pickMultipleImages();
//            ImageModel image= new ImageModel(2, "image1", 100, 100);
//            Log.v("Image", "Image added");
//            mainController.getImageController().add(image);
//            mainController.getImageController().getAll();

            firebaseManager = new FirebaseManager(this);
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
        imageAdapter.notifyDataSetChanged();

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
                Log.v("Image", "Image: " + uri.toString());
                imagePaths.add(uri.toString());


                // Create file metadata including the content type
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpg")
                        .build();
                // Upload file and metadata to the path 'images/filepath'
                StorageReference riversRef = firebaseManager.getStorage().getReference().child("images/"+uri.getLastPathSegment());
                UploadTask uploadTask = riversRef.putFile(uri, metadata);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getMetadata();
                    }
                });
                Task<Uri> urlTask = uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return riversRef.getDownloadUrl();
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Log.v("Image", "Image: " + downloadUri.toString());
                        } else {
                            // Handle failures
                            // ...
                        }
                    }
                });

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




