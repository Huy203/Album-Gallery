package com.example.albumgallery.controller;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.helper.DatabaseHelper;
import com.example.albumgallery.model.AlbumModel;
import com.example.albumgallery.model.Model;
import com.example.albumgallery.view.activity.CreateAlbumActivity;
import com.example.albumgallery.view.listeners.BackgroundProcessingCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumController implements Controller {
    //    private AlbumModel album;
    private final static String TAG = "Album";
    private final DatabaseHelper dbHelper;
    private final Activity activity;
    private final FirebaseManager firebaseManager;
    private AlbumModel currentModel;


//    public AlbumController(Context context) {
////        album = new AlbumModel(context);
//        this.dbHelper = new DatabaseHelper(context);
////        this.firebaseManager = FirebaseManager.getInstance(activity);
//
//    }

    public AlbumController(Activity activity) {
        this.activity = activity;
        this.dbHelper = new DatabaseHelper(activity);
        this.firebaseManager = FirebaseManager.getInstance(activity);
        this.currentModel = new AlbumModel();
    }

    private DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public List<AlbumModel> getAllAlbums() {
        // Get all albums
        List<String> data = dbHelper.getAll("Album");
        Log.d("check delete data", String.valueOf(data));
        List<AlbumModel> albumModels = new ArrayList<>();
        for (String s : data) {
            String[] temp = s.split(",");
            albumModels.add(new AlbumModel(temp[0], temp[1], Integer.parseInt(temp[2]), temp[4], temp[5], Integer.parseInt(temp[7])));
        }
        return albumModels;
    }

    @Override
    public void insert(Model model) {
//        dbHelper.insert("Album", model);
//        dbHelper.close();

        firebaseManager.getFirebaseHelper().add(TAG, model, firebaseManager.getFirebaseAuth().getCurrentUser().getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        currentModel.setId(documentReference.getId());
//                        idSelectedImages.add(dbHelper.insert("Image", currentModel));
                        dbHelper.insert("Album", currentModel);
                        update("id", documentReference.getId(), "id = '" + documentReference.getId() + "'");
                        activity.runOnUiThread(() -> {
                            ((CreateAlbumActivity) activity).backAction();
                        });
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
//        dbHelper.update("Album", column, value, where);
//        dbHelper.close();
        dbHelper.update("Album", column, value, where);
        dbHelper.close();
        Map<String, Object> data = new HashMap<>();
        data.put(column, value);
        int start = where.indexOf("'") + 1;
        int end = where.lastIndexOf("'");
        String id = where.substring(start, end);
        Log.v(TAG, "Updating document with ID: " + id + " " + data.toString());
        firebaseManager.getFirebaseHelper().update(TAG, data, id, firebaseManager.getFirebaseAuth().getCurrentUser().getUid());
    }

    @Override
    public void delete(String where) {
        dbHelper.delete("Album", where);
        dbHelper.close();
    }

    //    public void handleCreateAlbum(AlbumModel albumModel) {
//        currentModel = albumModel;
//        insert(currentModel);
//    }
    public void addAlbum(String name, String password, int numOfImages, String thumbnail) {
        this.currentModel = new AlbumModel(name, password, numOfImages);
        currentModel.setThumbnail(thumbnail);
        this.insert(currentModel);
    }

    public boolean isAlbumNameExists(String albumName) {
        return dbHelper.isAlbumNameExists(albumName);
    }

    public String getAlbumIdByName(String albumName) {
        return dbHelper.getAlbumIdByName(albumName);
    }

    public List<String> getAlbumNames() {
//        List<String> albumNames = dbHelper.select("Album","name", null);
        List<String> albumNames = dbHelper.getFromAlbum("name");
        return albumNames;
    }

    public String getLastAlbumId() {
        String res = dbHelper.getLastId("Album");
        return res;
    }

    public String getPasswordByAlbumName(String albumName) {
        return dbHelper.getPasswordByAlbumName(albumName);
    }

    public void updateThumbnailByAlbumName(String albumName, String thumbnail) {
        dbHelper.updateThumbnailByAlbumName(albumName, thumbnail);
    }

    public List<String> getAllThumbnails() {
        return dbHelper.getAllThumbnails();
    }

    public void deleteAlbum(String name) {
        // Delete an album
        dbHelper.delete("Album", "name='" + name + "'");
        dbHelper.close();
    }

    public void removeAlbumPasswordByName(String albumName) {
        dbHelper.removeAlbumPasswordByName(albumName);
    }

    public void editAlbum() {
        // Edit an album
    }

    public void viewAlbum() {
        // View an album
    }

    public void shareAlbum() {
        // Share an album
    }

    public void favouriteAlbum() {
        // Favourite an album
    }

    public void unfavouriteAlbum() {
        // Unfavourite an album
    }

    public AlbumModel getAlbumById(String id) {
        String data = dbHelper.getById("Album", id);
        String[] temp = data.split(",");
        Log.d("temp data", Arrays.toString(temp));
        return new AlbumModel(temp[0], temp[1], Integer.parseInt(temp[2]), temp[4], temp[5], Integer.parseInt(temp[7]));
    }

    public List<String> getAllAlbumIds() {
        return dbHelper.getFromAlbum("id");
    }


    public void loadFromFirestore() {
        List<String> albumIdsIdsInDatabase = getAllAlbumIds();
        List<String> albumIdsInFirestore = new ArrayList<>();
        firebaseManager.getFirebaseHelper().getAll(firebaseManager.getFirebaseAuth().getCurrentUser().getUid(), TAG)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            albumIdsInFirestore.add(document.getId());
//                            Log.d("Firebase album", document.getId());
                        }
                        if (albumIdsIdsInDatabase.size() == 0) {
                            for (String albumId : albumIdsInFirestore) {
                                if (!albumIdsIdsInDatabase.contains(albumId)) {
                                    firebaseManager.getFirebaseHelper().getById(TAG, albumId, firebaseManager.getFirebaseAuth().getCurrentUser().getUid())
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot != null) {
                                                    currentModel = new AlbumModel(
                                                            documentSnapshot.get("id").toString(),
                                                            documentSnapshot.get("name").toString(),
                                                            Integer.parseInt(documentSnapshot.get("capacity").toString()),
                                                            documentSnapshot.get("created_at").toString(),
                                                            documentSnapshot.get("notice").toString(),
                                                            documentSnapshot.get("ref").toString(),
                                                            documentSnapshot.get("password").toString(),
                                                            Integer.parseInt(documentSnapshot.get("num_of_images").toString()),
//                                                            documentSnapshot.get("is_deleted").equals("1") ? true : false,
                                                            Integer.parseInt(documentSnapshot.get("is_deleted").toString()),
                                                            documentSnapshot.get("thumbnail").toString());
                                                    dbHelper.insert("Album", currentModel);
//                                                    activity.runOnUiThread(() -> {
//                                                        ((MainFragmentController) activity).onBackgroundTaskCompleted();
//                                                    });
                                                } else {
                                                    Log.d("Firebase album", "document is null");
                                                }
                                            });
                                }
                            }
                        }
//                        if (allTasksCompleted(uploadTasks)) {
//                            activity.runOnUiThread(() -> {
//                                ((MainFragmentController) activity).onBackgroundTaskCompleted();
//                            });
//                        }
                    }
                    return albumIdsInFirestore;
                });
    }

}
