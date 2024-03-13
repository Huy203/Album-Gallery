package com.example.albumgallery.controller;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.albumgallery.DatabaseHelper;
import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.model.Model;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

public class ImageController {
    private DatabaseHelper dbHelper;

    public ImageController(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void getAll() {
        // Get all images
        Log.v("ImageController", "Getting all images");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Image", null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Log.v("ImageController", "Image: " + cursor.getString(0));
                cursor.moveToNext();
            }
        }
    }

    public void create(Model model) {
        // Add an image
        ContentValues values = new ContentValues();
        dbHelper.insert("Image", model);
        dbHelper.close();
    }

    public void update(String column, String value, String where) {
        // Update an image
        dbHelper.update("Image", column, value, where);
        dbHelper.close();
    }

    public void delete(String where) {
        // Delete an image
//        dbHelper.delete("Image", where);
        dbHelper.close();
    }

    public void handleImagePicked(@NonNull Intent data, List<String> imagePaths, FirebaseManager firebaseManager) {
        if (data.getData() != null) {
            Uri uri = data.getData(); // The uri with the location of the file
            imagePaths.add(uri.toString());
        } else if (data.getClipData() != null) {
            ClipData clipData = data.getClipData();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                ClipData.Item item = clipData.getItemAt(i);
                Uri uri = item.getUri(); // The uri with the location of the file
                imagePaths.add(uri.toString());

                uploadImage(uri, firebaseManager);


            }
        }
    }

    private void uploadImage(Uri uri, FirebaseManager firebaseManager) {

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
        // Upload file and metadata to the path 'images/image+filepath'
        StorageReference riversRef = firebaseManager.getStorage().getReference().child("images/image" + uri.getLastPathSegment());
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
                    Uri downloadUri = task.getResult(); // The uri with the location of the file in firebase
//                    update("url", downloadUri.toString(), "id = 1");
                } else {
                    // Handle failures
                    // ...
                }
            }
        });

    }
// https://firebasestorage.googleapis.com/v0/b/album-gallery-70d05.appspot.com/o/images%2Fimage2045421098?alt=media&token=83d0bf36-1a8f-4b7a-9bb5-03930a0a9f3f
    public void getImages() {
        // Get all images
        Log.v("ImageController", "Getting all images");
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Image", null, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Log.v("ImageController", "Image: " + cursor.getString(0));
                cursor.moveToNext();
            }
        }
    }
}
