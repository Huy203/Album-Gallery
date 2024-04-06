package com.example.albumgallery.controller;

import static android.app.Activity.RESULT_OK;
import static com.example.albumgallery.utils.Constant.REQUEST_CODE_PICK_MULTIPLE_IMAGES;
import static com.example.albumgallery.utils.Constant.imageExtensions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.albumgallery.view.activity.BackgroundProcessingCallback;
import com.example.albumgallery.DatabaseHelper;
import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.model.Model;
import com.example.albumgallery.view.activity.HomeScreen;
import com.example.albumgallery.view.activity.MainFragmentController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ImageController implements Controller {
    private static final int CAMERA_REQUEST_CODE = 1000;
    final String[] PROJECTION = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATE_ADDED,};
    private final Activity activity;
    private final DatabaseHelper dbHelper;
    private final FirebaseManager firebaseManager;
    private final List<Long> idSelectedImages = new ArrayList<>();

    public ImageController(Activity activity) {
        this.activity = activity;
        this.dbHelper = new DatabaseHelper(activity);
        this.firebaseManager = FirebaseManager.getInstance(activity);
    }

    private DatabaseHelper getDbHelper(){
        return dbHelper;
    }

    public void create(String name, int width, int height, long capacity, String dateAdded) {
        ImageModel imageModel = new ImageModel(name, width, height, capacity, dateAdded);
        this.insert(imageModel);
    }

    @Override
    public void insert(Model model) {
        idSelectedImages.add(dbHelper.insert("Image", model));
        dbHelper.close();
        firebaseManager.getStorage();
        // FirebaseStorage storage = FirebaseStorage.getInstance();
    }

    @Override
    public void update(String column, String value, String where) {
        // Update an image
        dbHelper.update("Image", column, value, where);
        dbHelper.close();
    }

    @Override
    public void delete(String where) {
        // Delete an image
        dbHelper.delete("Image", where);
        dbHelper.close();
    }

    public void pickMultipleImages(BackgroundProcessingCallback callback) {
        idSelectedImages.clear(); // Clear the list of image previously selected
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        activity.startActivityForResult(intent, REQUEST_CODE_PICK_MULTIPLE_IMAGES);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_MULTIPLE_IMAGES && resultCode == RESULT_OK && data != null) {
            handleImagePicked(data);
        }
        // xử lý ảnh chụp từ camera trong app
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            if (bitmap != null) {
                Uri imageUri = convertFromBitmapToURI(activity, bitmap);
                handleImageFromCamera(imageUri);

            } else {
                Log.d("Camera in app", "Bitmap Image is null");
            }
        }
    }

    private Uri convertFromBitmapToURI(Context inContext, Bitmap inImage) {
        String imageName = "IMG_" + System.currentTimeMillis() + ".jpg";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, imageName, null);
        return Uri.parse(path);
    }

    private void handleImagePicked(Intent data) {
        Log.v("Image", "Image picked" + " " + data.getData());
        List<Uri> imageUris = new ArrayList<>();
        if (data.getData() != null) {
            // Single image selected
            imageUris.add(data.getData());
        } else if (data.getClipData() != null) {
            // Multiple images selected
            ClipData clipData = data.getClipData();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                imageUris.add(clipData.getItemAt(i).getUri());
            }
        }

        List<Task<Uri>> uploadTasks = new ArrayList<>();
        for (Uri uri : imageUris) {
            retrieveDataImageFromURL(uri);
            Log.v("Image", "Image selected" + " " + uri);
            uploadTasks.add(uploadImage(uri));
        }

        for (Task<Uri> task : uploadTasks) {
            task.addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    try {
                        Uri uriImage = task1.getResult(); // The uri with the location of the file in firebase
                        update("ref", uriImage.toString(), "id = " + idSelectedImages.get(uploadTasks.indexOf(task1))); // Update the reference of the image
                        Log.v("Image", "Image uploaded" + " at " + idSelectedImages.get(uploadTasks.indexOf(task1)));
                        if (allTasksCompleted(uploadTasks)) {
                            activity.runOnUiThread(() -> {
                                ((MainFragmentController) activity).onBackgroundTaskCompleted();
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            });
        }
    }

    private void handleImageFromCamera(Uri imageUri) {
        retrieveDataImageFromURL(imageUri);
        Log.d("Camera in app", "here");
        try {
            Long id = dbHelper.getLastId("Image");
            String id_string = Long.toString(id);
            Task<Uri> uploadTask = uploadImage(imageUri);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri firebaseURI = task.getResult();
                    Log.d("test firebase uri", firebaseURI.toString());
                    update("ref", firebaseURI.toString(), "id = " + id_string);
                    activity.runOnUiThread(() -> {
                        ((MainFragmentController) activity).onBackgroundTaskCompleted();
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Camera in app", "add and update image fail");
        }
    }

    @SuppressLint("Range")
    private void retrieveDataImageFromURL(Uri uri) {
        try (Cursor cursor = activity.getContentResolver().query(uri, PROJECTION, null, null, null)) {
            long id;
            String name;
            int width, height;
            long capacity, dateAdded;

            if (cursor != null && cursor.moveToFirst()) {
                // Get the column index of the image
                id = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                capacity = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                dateAdded = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                // Convert milliseconds to a more readable format (optional)
                @SuppressLint("SimpleDateFormat") String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(dateAdded * 1000));

                this.create(name, width, height, capacity, formattedDate); // Create an image

                Log.v("Image", "Image added" + " " + id + " " + name + " " + capacity + " " + dateAdded + formattedDate);
            }
        }
    }

    private Task<Uri> uploadImage(Uri uri) {
        Log.v("Image", "Uploading image" + " " + uri);
        String extensionName = getExtensionName(uri);

        // Create file metadata including the content type
        // Set extension of the file is "jpg"
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
        // Upload file and metadata to the path 'images/image+filepath'
        StorageReference imageRef = firebaseManager.getStorage().getReference().child("images/image" + uri.getLastPathSegment() + "." + (imageExtensions.contains(extensionName) ? extensionName : "jpg"));
        UploadTask uploadTask = imageRef.putFile(uri, metadata);

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
        return uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            Log.v("Image", "Image uploaded" + " " + imageRef.getDownloadUrl());
            return imageRef.getDownloadUrl();

        });
    }

    private boolean allTasksCompleted(List<Task<Uri>> tasks) {
        for (Task<Uri> task : tasks) {
            if (!task.isSuccessful()) {
                return false;
            }
        }
        return true;
    }
    private boolean allTasksCompletedGeneric(List<Task> tasks) {
        for (Task<Uri> task : tasks) {
            if (!task.isSuccessful()) {
                return false;
            }
        }
        return true;
    }

    public String getExtensionName(Uri uri) {
        String fileName = uri.getPathSegments().get(uri.getPathSegments().size() - 1);
        return fileName.split("\\.")[fileName.split("\\.").length - 1];
    }

    public List<ImageModel> getAllImages() {
        List<String> data = dbHelper.getAll("Image");
        List<ImageModel> imageModels = new ArrayList<>();
        for (String s : data) {
            String[] temp = s.split(",");
            imageModels.add(new ImageModel(Integer.parseInt(temp[0]), temp[1], Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), Long.parseLong(temp[4]), temp[5], temp[6], temp[7], temp[8], Boolean.parseBoolean(temp[9]), Boolean.parseBoolean(temp[10])));
        }
        return imageModels;
    }

    public List<String> getAllImageURLs() {
        return dbHelper.getAllRef("Image");
    }
    public ImageModel getImageById(long id) {
        String data = dbHelper.getById("Image", id);
        String[] temp = data.split(",");
        return new ImageModel(Integer.parseInt(temp[0]),temp[1], Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), Long.parseLong(temp[4]), temp[5], temp[6], temp[7], temp[8], Boolean.parseBoolean(temp[9]), Boolean.parseBoolean(temp[10]));
    }

    public long getIdByRef(String ref) {
        return dbHelper.getId("Image", "ref = '" + ref + "'");
    }

    public List<String> getAllImageURLsSortByDate() {
        return dbHelper.selectImagesSortByDate("Image", "ref", "descending");
    }
    public List<String> getAllImageIds() {
        return dbHelper.getFromImage("id");
    }

    public String getImageRefById(long imageId) {
        return dbHelper.getImageRefById(imageId);
    }
    public List<String> getSelectedImageURLs() {
        final String replace = idSelectedImages.toString().replace("[", "").replace("]", "");
        Log.v("Image", "Selected images: " + "ref" + "id IN (" + replace + ")");
        return dbHelper.select("Image", "ref", "id IN (" + replace + ")");
    }

    private String parseURL(String url) {
        String filename = null;
        try {
            // Parse the URL
            URI uri = new URI(url);

            // Extract filename
            filename = Paths.get(uri.getPath()).getFileName().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        filename = "images/" + filename;
        return filename;
    }

    public void deleteSelectedImage(String imageURL){
        String URL = parseURL(imageURL);
        Log.d("URL", URL);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference to the file to delete
        StorageReference desertRef = storageRef.child(URL);

        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                delete("ref = '" + imageURL + "'");
                Toast.makeText(activity, "Image deleted successfully", Toast.LENGTH_SHORT).show();
                // You may want to update your local data or UI here if necessary.

                Intent intent = new Intent(activity, MainFragmentController.class);
                activity.startActivity(intent);
                activity.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(activity, "Image deleted failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String checkExistURL(String longImageURL) {
        String[] parts = longImageURL.split("/");
        String imageURL = parts[parts.length - 1];

        getDbHelper().select("image", "ref", null);

        // Get all image URLs
        List<String> imageURLs = getAllImageURLs();
        Log.d("size", String.valueOf(imageURLs.size()));
        Log.d("ImageURLs", "Image URLs: " + imageURLs);
        Log.d("ImagePath", "Image Path: " + imageURL);

        // Check if the current imageURL exists in the list
        for (String url : imageURLs) {
            if (url.contains(imageURL)) {
                return url;
            }
        }
        return null;
    }
    public List<String> getImagePaths() {
        return getAllImageURLs();
    }
//    public void deleteSelectedImageAtHomeScreeen(List<Task> imageURLs){
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        // Create a storage reference from our app
//        StorageReference storageRef = storage.getReference();
//
//        for (Task taskImageURL : imageURLs) {
//            if (taskImageURL.isSuccessful()) {
//                String imageURL = taskImageURL.getResult().toString();
//                Log.d("Image task", imageURL);
//                String URL = parseURL(imageURL);
//
//                Log.d("delete url", URL);
//
//                // Create a reference to the file to delete
//                StorageReference desertRef = storageRef.child(URL);
//
//                // Delete the file
//                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        // File deleted successfully
//                        delete("ref = '" + imageURL + "'");
//                        if (allTasksCompletedGeneric(imageURLs)) {
//                            activity.runOnUiThread(() -> {
//                                ((MainFragmentController) activity).onBackgroundTaskCompleted();
//                            });
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Uh-oh, an error occurred!
//                        Toast.makeText(activity, "Image deleted failed", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }
//    }

    public void deleteSelectedImageAtHomeScreeen(List<Task> imageURLs){
        for (Task taskImageURL : imageURLs) {
            if (taskImageURL.isSuccessful()) {
                String imageURL = taskImageURL.getResult().toString();
                Log.d("Image task", imageURL);
                String URL = parseURL(imageURL);

                long imageID = dbHelper.getImageIdByURL(imageURL);

                setDeleteAtHomeScreen(imageID, true);

                Log.d("delete image url", imageURL);
                Log.d("delete id", String.valueOf(imageID));

                // Delete the file
                if (allTasksCompletedGeneric(imageURLs)) {
                    activity.runOnUiThread(() -> {
                        ((MainFragmentController) activity).onBackgroundTaskCompleted();
                    });
                }
            }
        }
    }
    public long getTotalCapacityFromImageIDs(List<Long> imageIDs){
        return dbHelper.getTotalCapacityFromImageIDs(imageIDs);
    }
    public void toggleDeleteImage(long imageId) {
        dbHelper.toggleDeleteImage(imageId);
    }
    public void setDelete(long imageId, boolean isDelete) {
        dbHelper.setDelete(imageId, isDelete);

        Intent intent = new Intent(activity, MainFragmentController.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public void setDeleteAtHomeScreen(long imageId, boolean isDelete) {
        dbHelper.setDelete(imageId, isDelete);
    }
    public boolean isDeleteImage(long imageId) {
        return dbHelper.isDeleteImage(imageId);
    }
    public List<String> getAllDeleteImageRef() {
        return dbHelper.getAllDeleteImageRef();
    }
}
