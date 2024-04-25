package com.example.albumgallery.controller;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.helper.DatabaseHelper;
import com.example.albumgallery.model.AlbumModel;
import com.example.albumgallery.model.Model;
import com.example.albumgallery.view.activity.CreateAlbumActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumController implements Controller {
    private final static String TAG = "Album";
    private final DatabaseHelper dbHelper;
    private final Activity activity;
    private final FirebaseManager firebaseManager;
    private AlbumModel currentModel;

    public AlbumController(Activity activity) {
        this.activity = activity;
        this.dbHelper = new DatabaseHelper(activity);
        this.firebaseManager = FirebaseManager.getInstance(activity);
        this.currentModel = new AlbumModel();
    }

    @Override
    public void insert(Model model) {
        firebaseManager.getFirebaseHelper().add(TAG, model, firebaseManager.getFirebaseAuth().getCurrentUser().getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        currentModel.setId(documentReference.getId());
                        dbHelper.insert("Album", currentModel);
                        update("id", documentReference.getId(), "id = '" + documentReference.getId() + "'");
                        activity.runOnUiThread(() -> {
                            ((CreateAlbumActivity) activity).addToImageAlbum();
                            ((CreateAlbumActivity) activity).backAction();
                        });
                        dbHelper.close();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    public void update(String column, String value, String where) {
        dbHelper.update("Album", column, value, where);
        dbHelper.close();
        Map<String, Object> data = new HashMap<>();
        data.put(column, value);
        int start = where.indexOf("'") + 1;
        int end = where.lastIndexOf("'");
        String id = where.substring(start, end);
        firebaseManager.getFirebaseHelper().update(TAG, data, id, firebaseManager.getFirebaseAuth().getCurrentUser().getUid());
    }

    @Override
    public void delete(String where) {
        dbHelper.delete("Album", where);
        dbHelper.close();
    }

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

    public AlbumModel getAlbumById(String id) {
        String data = dbHelper.getById("Album", id);
        String[] temp = data.split(",");
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
                                                            Integer.parseInt(documentSnapshot.get("is_deleted").toString()),
                                                            documentSnapshot.get("thumbnail").toString());
                                                    dbHelper.insert("Album", currentModel);
                                                } else {
                                                    Log.d("Firebase album", "document is null");
                                                }
                                            });
                                }
                            }
                        }
                    }
                    return albumIdsInFirestore;
                });
    }
}
