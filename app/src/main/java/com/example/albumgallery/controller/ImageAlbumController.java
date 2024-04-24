package com.example.albumgallery.controller;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.helper.DatabaseHelper;
import com.example.albumgallery.model.ImageAlbumModel;
import com.example.albumgallery.model.Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private DatabaseHelper getDbHelper() {
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
        firebaseManager.getFirebaseHelper().add(TAG, model, firebaseManager.getFirebaseAuth().getCurrentUser().getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        dbHelper.insert("Image_Album", model);
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

    public void loadFromFirestore() {
        List<Map<String, List<String>>> albumIdsIdsInDatabase = getAllAlbumIdsDatabase();
        List<String> albumIdsInFirestore = new ArrayList<>();
        List<String> imageIdsInFirestore = new ArrayList<>();

        firebaseManager.getFirebaseHelper().getAll(firebaseManager.getFirebaseAuth().getCurrentUser().getUid(), TAG)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            imageIdsInFirestore.add(document.get("image_id").toString());
                            albumIdsInFirestore.add(document.get("album_id").toString());
                        }

                        List<Map<String, List<String>>> albumIdsIdsInFirestore = getAllAlbumIdsFirestore(albumIdsInFirestore, imageIdsInFirestore);

                        if (albumIdsIdsInDatabase.isEmpty()) {
                            insertNewAlbums(albumIdsIdsInFirestore);
                        } else if (albumIdsIdsInDatabase.size() < albumIdsIdsInFirestore.size()) {
                            insertNewAlbums(getNewAlbums(albumIdsIdsInDatabase, albumIdsIdsInFirestore));
                        }
                    }
                    return albumIdsInFirestore;
                });
    }

    private void insertNewAlbums(List<Map<String, List<String>>> albumIdsIdsInFirestore) {
        for (Map<String, List<String>> albumId : albumIdsIdsInFirestore) {
            for (Map.Entry<String, List<String>> entry : albumId.entrySet()) {
                for (String imageId : entry.getValue()) {
                    insertImageAlbum(entry.getKey(), imageId);
                }
            }
        }
    }

    private List<Map<String, List<String>>> getNewAlbums(List<Map<String, List<String>>> albumIdsIdsInDatabase, List<Map<String, List<String>>> albumIdsIdsInFirestore) {
        List<Map<String, List<String>>> newAlbums = new ArrayList<>(albumIdsIdsInFirestore);
        newAlbums.removeAll(albumIdsIdsInDatabase);
        return newAlbums;
    }

    private void insertImageAlbum(String albumId, String imageId) {
        firebaseManager.getFirebaseHelper().getById(TAG, imageId, firebaseManager.getFirebaseAuth().getCurrentUser().getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null) {
                        currentModel = new ImageAlbumModel(imageId, albumId);
                        dbHelper.insert("ImageAlbum", currentModel);
                    } else {
                        Log.d("Firebase album", "document is null");
                    }
                });
    }

    private List<Map<String, List<String>>> getAllAlbumIdsFirestore(List<String> albumIdsInFirestore, List<String> imageIdsInFirestore) {
        List<Map<String, List<String>>> data = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        String currentIdAlbum = null;

        for (int i = 0; i < albumIdsInFirestore.size(); i++) {
            String albumId = albumIdsInFirestore.get(i);
            String imageId = imageIdsInFirestore.get(i);

            if (!albumId.equals(currentIdAlbum)) {
                if (currentIdAlbum != null) {
                    data.add(map);
                    map = new HashMap<>();
                }
                currentIdAlbum = albumId;
            }
            map.computeIfAbsent(albumId, k -> new ArrayList<>()).add(imageId);
        }
        if (!map.isEmpty()) {
            data.add(map);
        }
        return data;
    }

    private List<Map<String, List<String>>> getAllAlbumIdsDatabase() {
        List<String> imageAlbumList = dbHelper.getAll("Image_Album");
        if (imageAlbumList.size() == 0) {
            return new ArrayList<>();
        } else {
            List<Map<String, List<String>>> data = new ArrayList<>();
            Map<String, List<String>> map = new HashMap<>();
            String currentIdAlbum = null;

            for (String item : dbHelper.getAll("Image_Album")) {
                String[] temp = item.split(",");
                String albumId = temp[1];
                String imageId = temp[0];

                if (!albumId.equals(currentIdAlbum)) {
                    if (currentIdAlbum != null) {
                        data.add(map);
                        map = new HashMap<>();
                    }
                    currentIdAlbum = albumId;
                }
                map.computeIfAbsent(albumId, k -> new ArrayList<>()).add(imageId);
            }

            if (!map.isEmpty()) {
                data.add(map);
            }
            return data;
        }
    }
}