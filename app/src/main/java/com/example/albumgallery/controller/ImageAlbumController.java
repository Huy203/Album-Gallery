package com.example.albumgallery.controller;

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.helper.DatabaseHelper;
import com.example.albumgallery.model.AlbumModel;
import com.example.albumgallery.model.ImageAlbumModel;
import com.example.albumgallery.model.Model;
import com.example.albumgallery.view.activity.CreateAlbumActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class ImageAlbumController implements Controller {
    private final static String TAG = "ImageAlbum";
    private ImageAlbumModel currentModel;
    private final DatabaseHelper dbHelper;
    private final FirebaseManager firebaseManager;


//    public ImageAlbumController(Context context) {
//        ImageAlbum = new ImageAlbumModel(context);
//        this.dbHelper = new DatabaseHelper(context);
//    }
    public ImageAlbumController(Activity activity) {
        this.dbHelper = new DatabaseHelper(activity);
        this.currentModel = new ImageAlbumModel();
        this.firebaseManager = FirebaseManager.getInstance(activity);
    }
    private DatabaseHelper getDbHelper(){
        return dbHelper;
    }
    public List<ImageAlbumModel> getAllImageAlbums() {
        // Get all ImageAlbums
        List<String> data = dbHelper.getAll("Image_Album");
        Log.d("check delete data", String.valueOf(data));
        List<ImageAlbumModel> ImageAlbumModels = new ArrayList<>();
        for (String s : data) {
            String[] temp = s.split(",");
            ImageAlbumModels.add(new ImageAlbumModel(temp[0], temp[1]));
        }
        return ImageAlbumModels;
    }
    @Override
    public void insert(Model model) {
//        dbHelper.insert("Image_Album", model);
//        dbHelper.insertByCustomId("Image_Album", model);
//        dbHelper.close();

        firebaseManager.getFirebaseHelper().add(TAG, model, firebaseManager.getFirebaseAuth().getCurrentUser().getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        currentModel.setId(documentReference.getId());
                        Log.d("Firebase id", documentReference.getId());
//                        idSelectedImages.add(dbHelper.insert("Image", currentModel));
                        dbHelper.insert("Image_Album", currentModel);
//                        update("id", documentReference.getId(), "id = '" + documentReference.getId() + "'");
//                        activity.runOnUiThread(() -> {
//                            ((CreateAlbumActivity) activity).backAction();
//                        });
                        dbHelper.close();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    public void update(String column, String value, String where) {
        dbHelper.update("Image_Album", column, value, where);
        dbHelper.close();
    }

    @Override
    public void delete(String where) {
        dbHelper.delete("Image_Album", where);
        dbHelper.close();
    }

    public void addImageAlbum(String id_image, String id_album) {
        // Add an ImageAlbum
//        ImageAlbumModel imageAlbumModel = new ImageAlbumModel(id_image, id_album);
//        this.insert(imageAlbumModel);
        this.currentModel = new ImageAlbumModel(id_image, id_album);
        Log.d("Firebase curretn model", currentModel.getImage_id());
        Log.d("Firebase curretn model", currentModel.getAlbum_id());
        this.insert(currentModel);
    }

    public List<String> getImageIdsByAlbumId(String albumId) {
        return dbHelper.getImageIdsByAlbumId(albumId);
    }

    public void deleteImageAlbum(String id_album) {
        // Delete an ImageAlbum
        this.delete("album_id = " + id_album);
    }

    public void editImageAlbum() {
        // Edit an ImageAlbum
    }

    public void viewImageAlbum() {
        // View an ImageAlbum
    }

    public void shareImageAlbum() {
        // Share an ImageAlbum
    }

    public void favouriteImageAlbum() {
        // Favourite an ImageAlbum
    }

    public void unfavouriteImageAlbum() {
        // Unfavourite an ImageAlbum
    }
}
