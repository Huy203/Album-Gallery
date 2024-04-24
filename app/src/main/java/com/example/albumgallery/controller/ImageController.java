package com.example.albumgallery.controller;

import static android.app.Activity.RESULT_OK;
import static com.example.albumgallery.utils.Constant.REQUEST_CODE_CAMERA;
import static com.example.albumgallery.utils.Constant.REQUEST_CODE_EDIT_IMAGE;
import static com.example.albumgallery.utils.Constant.REQUEST_CODE_PICK_MULTIPLE_IMAGES;
import static com.example.albumgallery.utils.Constant.imageExtensions;
import static com.example.albumgallery.utils.Utilities.byteArrayToBitmap;
import static com.example.albumgallery.utils.Utilities.convertFromBitmapToUri;
import static com.example.albumgallery.utils.Utilities.getImageAddedDate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.helper.DatabaseHelper;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.model.Model;
import com.example.albumgallery.utils.QRCodeRecognization;
import com.example.albumgallery.utils.textRecognization.TextRecognization;
import com.example.albumgallery.view.activity.MainFragmentController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ImageController implements Controller {
    private final static String TAG = "Image";
    final String[] PROJECTION = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.WIDTH, MediaStore.Images.Media.HEIGHT, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATE_ADDED};
    private final Activity activity;
    private final DatabaseHelper dbHelper;
    private final FirebaseManager firebaseManager;
    private final List<String> idSelectedImages = new ArrayList<>();
    private final CollectionReference collection;
    private ImageModel currentModel;
    private boolean complete = false;

    public ImageController(Activity activity) {
        this.activity = activity;
        this.dbHelper = new DatabaseHelper(activity);
        this.firebaseManager = FirebaseManager.getInstance(activity);
        this.collection = firebaseManager.getFirestore().collection(TAG);
        this.currentModel = new ImageModel();
    }

    private DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public FirebaseManager getFirebaseManager() {
        return firebaseManager;
    }

    @Override
    public void insert(Model model) {
        firebaseManager.getFirebaseHelper().add(TAG, model, firebaseManager.getFirebaseAuth().getCurrentUser().getUid())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        currentModel.setId(documentReference.getId());
                        idSelectedImages.add(dbHelper.insert("Image", currentModel));
                        update("id", documentReference.getId(), "id = '" + documentReference.getId() + "'");
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
        dbHelper.update("Image", column, value, where);
        dbHelper.close();
        Map<String, Object> data = new HashMap<>();
        data.put(column, value);
        int start = where.indexOf("'") + 1;
        int end = where.lastIndexOf("'");
        String id = where.substring(start, end);
        Log.v(TAG, "Updating document with ID: " + id + " " + data.toString());
        firebaseManager.getFirebaseHelper().update(TAG, data, id, firebaseManager.getFirebaseAuth().getCurrentUser().getUid())
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        complete = true;
                        activity.runOnUiThread(() -> {
                            ((MainFragmentController) activity).onBackgroundTaskCompleted();
                        });
                    }
                    return null;
                });
    }

    @Override
    public void delete(String where) {
        // Delete an image
        dbHelper.delete("Image", where);
        dbHelper.close();
    }

    public ImageModel getImageById(String id) {
        String data = dbHelper.getById("Image", id);
        String[] temp = data.split(",");
        boolean isFavourited = temp[10].equals("1");
        boolean isDeleted = temp[9].equals("1");
        return new ImageModel(temp[0], temp[1], Integer.parseInt(temp[2]), Integer.parseInt(temp[3]), Long.parseLong(temp[4]), temp[5], temp[6], temp[7], temp[8], isDeleted, isFavourited);
    }

//    public ImageModel getImageById(String id) {
//        Task<Map<String, Object>> data = firebaseHelper.getById("Image", id);
//        data.addOnSuccessListener(documentSnapshot -> {
//            if (documentSnapshot != null) {
//                if (!currentModel.getId().equals(id))
//                    currentModel = new ImageModel(
//                            documentSnapshot.get("id").toString(),
//                            documentSnapshot.get("name").toString(),
//                            Integer.parseInt(documentSnapshot.get("width").toString()),
//                            Integer.parseInt(documentSnapshot.get("height").toString()),
//                            Long.parseLong(documentSnapshot.get("capacity").toString()),
//                            documentSnapshot.get("created_at").toString(),
//                            documentSnapshot.get("notice").toString(),
//                            documentSnapshot.get("ref").toString(),
//                            documentSnapshot.get("remain_time").toString(),
//                            (boolean) documentSnapshot.get("is_deleted"),
//                            (boolean) documentSnapshot.get("is_favourited"));
//            }
//        });
//        return currentModel;
//    }

    public String getIdByRef(String ref) {
        if (currentModel.getRef().equals(ref))
            return currentModel.getId();
        else
            return dbHelper.getId("Image", "ref = '" + ref + "'");
    }

    public List<String> getAllImageURLs() {
        return dbHelper.getAllRef("Image", null);
    }

    public List<String> getAllImageURLsUndeleted() {
        return dbHelper.getAllRef("Image", "is_deleted = 0");
    }

    public List<String> getAllImageURLsDeleted() {
        return dbHelper.getAllRef("Image", "is_deleted = 1");
    }

    public List<String> getAllImageURLsFavourited() {
        return dbHelper.getAllRef("Image", "is_favourited = 1");
    }

    public List<String> getAllImageURLsSortByDate() {
        return dbHelper.selectImagesSortByDate("Image", "ref", "descending");
    }

    public List<String> getAllImageURLsSortByDateAtBin() {
        return dbHelper.selectImagesSortByDateAtBin("Image", "ref", "descending");
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            Bitmap bitmap;
            switch (requestCode) {
                case REQUEST_CODE_PICK_MULTIPLE_IMAGES:
                    handleImagePicked(data);
                    break;
                case REQUEST_CODE_CAMERA:
                    bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    if (bitmap != null) {
                        Uri imageUri = convertFromBitmapToUri(activity, bitmap);
                        handleImage(imageUri, requestCode);
                    } else {
                        Log.d("Camera in app", "Bitmap Image is null");
                    }
                    break;
                case REQUEST_CODE_EDIT_IMAGE:
                    byte[] byteArray = data.getByteArrayExtra("imageByteArray");
                    bitmap = byteArrayToBitmap(byteArray);
                    if (bitmap != null) {
                        Uri imageUri = convertFromBitmapToUri(activity, bitmap);
                        handleImage(imageUri, requestCode);
                    } else {
                        Log.d("Edit image", "Bitmap Image is null");
                    }
                    break;
            }
        }
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
                        this.update("ref", uriImage.toString(), "id = '" + idSelectedImages.get(uploadTasks.indexOf(task)) + "'");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                }
            });
        }
    }

    private void handleImage(Uri imageUri, int requestCode) {
        retrieveDataImageFromURL(imageUri);
        try {
            Task<Uri> uploadTask = uploadImage(imageUri);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri firebaseURI = task.getResult();
                    update("ref", firebaseURI.toString(), "id = '" + currentModel.getId() + "'");
                    if (requestCode == REQUEST_CODE_CAMERA) {
                        activity.runOnUiThread(() -> {
                            ((MainFragmentController) activity).onBackgroundTaskCompleted();
                        });
                    } else {
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Camera in app", "add and update image fail");
        }
    }

    @SuppressLint("Range")
    private void retrieveDataImageFromURL(Uri uri) {
        String name = null, dateAdded = null;
        int width = 0, height = 0;
        long capacity = 0;

        // Get name, capacity and date added of the image
        try (Cursor cursor = activity.getContentResolver().query(uri, PROJECTION, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                capacity = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                // Convert milliseconds to a more readable format (optional)
                dateAdded = getImageAddedDate();
            }
        }
        // Get width and height of the image
        try {
            InputStream in = activity.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
            width = options.outWidth;
            height = options.outHeight;
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentModel = new ImageModel(name, width, height, capacity, dateAdded);
        insert(currentModel);
    }

    public Task<Uri> uploadImage(Uri uri) {
        Log.v("Image", "Uploading image" + " " + uri);
        String extensionName = getExtensionName(uri);

        // Create file metadata including the content type
        // Set extension of the file is "jpg"
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").build();
        // Upload file and metadata to the path 'images/image+filepath'
        StorageReference imageRef = firebaseManager.getStorage().getReference().child("/" + firebaseManager.getFirebaseAuth().getCurrentUser().getUid() + "/IMAGE" + uri.getLastPathSegment() + "." + (imageExtensions.contains(extensionName) ? extensionName : "jpg"));
        UploadTask uploadTask = imageRef.putFile(uri, metadata);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
        }).addOnSuccessListener(taskSnapshot -> taskSnapshot.getMetadata());
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


    public List<String> getAllImageIds() {
        return dbHelper.getFromImage("id");
    }

    public void loadFromFirestore() {
        List<String> imageIdsInDatabase = getAllImageIds();
        List<String> imageIdsInFirestore = new ArrayList<>();
        firebaseManager.getFirebaseHelper().getAll(firebaseManager.getFirebaseAuth().getCurrentUser().getUid(), TAG)
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            imageIdsInFirestore.add(document.getId());
                        }

                        if (imageIdsInDatabase.size() < imageIdsInFirestore.size()) {
                            for (String imageId : imageIdsInFirestore) {
                                if (!imageIdsInDatabase.contains(imageId)) {
                                    firebaseManager.getFirebaseHelper().getById(TAG, imageId, firebaseManager.getFirebaseAuth().getCurrentUser().getUid())
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot != null) {
                                                    currentModel = new ImageModel(
                                                            documentSnapshot.get("id").toString(),
                                                            documentSnapshot.get("name").toString(),
                                                            Integer.parseInt(documentSnapshot.get("width").toString()),
                                                            Integer.parseInt(documentSnapshot.get("height").toString()),
                                                            Long.parseLong(documentSnapshot.get("capacity").toString()),
                                                            documentSnapshot.get("created_at").toString(),
                                                            documentSnapshot.get("notice").toString(),
                                                            documentSnapshot.get("ref").toString(),
                                                            documentSnapshot.get("remain_time").toString(),
                                                            documentSnapshot.get("is_deleted").equals("1") ? true : false,
                                                            documentSnapshot.get("is_favourited").equals("1") ? true : false);
                                                    dbHelper.insert("Image", currentModel);
                                                    activity.runOnUiThread(() -> {
                                                        ((MainFragmentController) activity).onBackgroundTaskCompleted();
                                                    });

//                                                    activity.runOnUiThread(() -> {
//                                                        ((MainFragmentController) activity).onBackgroundTaskCompleted();
//                                                    });
                                                }
                                            });
                                }
                            }
                        }
//                        activity.runOnUiThread(() -> {
//                            ((MainFragmentController) activity).initiateVariable(String.valueOf(firebaseManager.getFirebaseAuth().getCurrentUser().getPhotoUrl()));
//                        });

//                        if (allTasksCompleted(uploadTasks)) {
//                            activity.runOnUiThread(() -> {
//                                ((MainFragmentController) activity).onBackgroundTaskCompleted();
//                            });
//                        }
                    }
                    return imageIdsInFirestore;
                });
    }


    public String getImageRefById(String imageId) {
        return dbHelper.getImageRefById(imageId);
    }

    public boolean toggleFavoriteImage(String imageId) {
        boolean isFavourited = getImageById(imageId).getIs_favourited();

        update("is_favourited", isFavourited ? "0" : "1", "id = '" + imageId + "'");
        return !isFavourited;
    }

    public boolean isFavoriteImage(String imageId) {
        return dbHelper.isFavoriteImage(imageId);
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
        // filename = "images/" + filename;
        // filename = "mdxa9wwR9Vbfo0XliX8ubCFY4Sz2/" + filename;
        filename = firebaseManager.getFirebaseAuth().getCurrentUser().getUid() + "/" + filename;
        return filename;
    }

    public void deleteSelectedImage(String imageURL) {
        String URL = parseURL(imageURL);
        Log.d("URL", URL);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference to the file to delete
        StorageReference desertRef = storageRef.child(URL);

        // Delete the file
        desertRef.delete().addOnSuccessListener(aVoid -> {
            // File deleted successfully
            delete("ref = '" + imageURL + "'");
            Toast.makeText(activity, "Image deleted successfully", Toast.LENGTH_SHORT).show();
            // You may want to update your local data or UI here if necessary.

//            Intent intent = new Intent(activity, MainFragmentController.class);
//            activity.startActivity(intent);
//            activity.finish();
        }).addOnFailureListener(exception -> {
            // Uh-oh, an error occurred!
            Toast.makeText(activity, "Image deleted failed", Toast.LENGTH_SHORT).show();
        });
    }

    public void restoreSelectedImage(String imageURL) {
        String imageID = dbHelper.getImageIdByURL(imageURL);
        dbHelper.setDelete(imageID, false);

        Intent intent = new Intent(activity, MainFragmentController.class);
        intent.putExtra("fragmentToLoad", "Bin");
        activity.startActivity(intent);
        activity.finish();
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

    public void deleteSelectedImageAtBin(List<Task> imageURLs) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        try {
            Log.v("Image", "Deleting images" + " " + imageURLs.size());
            for (Task taskImageURL : imageURLs) {
                if (taskImageURL.isSuccessful()) {
                    String imageURL = taskImageURL.getResult().toString();
                    Log.d("Image task", imageURL);
                    String URL = parseURL(imageURL);
                    Log.d("URL Image task", URL);

                    // Create a reference to the file to delete
                    StorageReference desertRef = storageRef.child(URL);
                    Log.d("Executing", "ok");

                    // Delete the file
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Before delete on db", "ok");
                            // File deleted successfully
                            delete("ref = '" + imageURL + "'");
                            Log.d("After delete on db", "ok");

                            if (allTasksCompletedGeneric(imageURLs)) {
                                Log.v("Image", "All images deleted" + activity);
                                activity.runOnUiThread(() -> {
                                    ((MainFragmentController) activity).onBackgroundTaskCompleted();
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                            Toast.makeText(activity, "Image deleted failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSelectedImageAtHomeScreeen(List<Task> imageURLs) {
        Log.d("get into delete at homescreen function", String.valueOf(activity));
        for (Task taskImageURL : imageURLs) {
            if (taskImageURL.isSuccessful()) {
                String imageURL = taskImageURL.getResult().toString();
                Log.d("Image task", imageURL);
                String URL = parseURL(imageURL);

                String imageID = dbHelper.getId("Image", "ref = '" + imageURL + "'");

                setDeleteAtHomeScreen(imageID, true);

                Log.d("delete image url", imageURL);
                Log.d("delete id", String.valueOf(imageID));

                // Delete the file
                if (allTasksCompletedGeneric(imageURLs)) {
                    Log.v("Image", "All images deleted" + activity);
//                    try{
//                        activity.runOnUiThread(() -> {
//                            ((MainFragmentController) activity).onBackgroundTaskCompleted();
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
    }

    public long getTotalCapacityFromImageIDs(List<String> imageIDs) {
        return dbHelper.getTotalCapacityFromImageIDs(imageIDs);
    }

    public boolean toggleDeleteImage(String imageId) {
        boolean isDeleted = getImageById(imageId).getIs_deleted();
        update("is_deleted", isDeleted ? "0" : "1", imageId);
        return !isDeleted;
    }

    public void setDelete(String imageId, boolean isDelete) {
        update("is_deleted", isDelete ? "1" : "0", "id = '" + imageId + "'");
    }

    public void setDeleteAtHomeScreen(String imageId, boolean isDelete) {
        update("is_deleted", isDelete ? "1" : "0", "id = '" + imageId + "'");
    }

    public boolean isDeleteImage(String imageId) {
        return getImageById(imageId).getIs_deleted();
    }

    public String recognizeQRCode(String uri) {
        QRCodeRecognization qrCodeRecognization = new QRCodeRecognization();
        try {
            return qrCodeRecognization.execute(uri).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void recognizeText(String uri) {
        TextRecognization textRecognization = new TextRecognization(activity);
        try {
            textRecognization.execute(uri).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//        FirebaseVisionImage image = null;
//        try {
//            image = FirebaseVisionImage.fromFilePath(activity, Uri.parse(uri));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
//                .getOnDeviceTextRecognizer();
//
//        Task<FirebaseVisionText> result =
//                detector.processImage(image)
//                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
//                            @Override
//                            public void onSuccess(FirebaseVisionText result) {
//
//                            }
//                        })
//                        .addOnFailureListener(
//                                new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        // Task failed with an exception
//                                        // ...
//                                    }
//                                });

    public Bitmap createQRCodeFromImage(String uri) {
        QRCodeRecognization qrCodeRecognization = new QRCodeRecognization();
        try {
            return qrCodeRecognization.generateQRCodeFromImage(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> selectImagesByNotice(String notice) {
        return dbHelper.selectImagesByNotice(notice);
    }

    public List<String> selectImageNamesByNotice(String notice) {
        return dbHelper.selectImageNamesByNotice(notice);
    }
}
