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

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.helper.DatabaseHelper;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.model.Model;
import com.example.albumgallery.utils.QRCodeRecognization;
import com.example.albumgallery.utils.textRecognization.TextRecognization;
import com.example.albumgallery.view.activity.MainFragmentController;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
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
    private ImageModel currentModel;

    public ImageController(Activity activity) {
        this.activity = activity;
        this.dbHelper = new DatabaseHelper(activity);
        this.firebaseManager = FirebaseManager.getInstance(activity);
        this.currentModel = new ImageModel();
    }

    private DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public Activity getActivity() {
        return activity;
    }

    public FirebaseManager getFirebaseManager() {
        return firebaseManager;
    }

    @Override
    public void insert(Model model) {
        firebaseManager.getFirebaseHelper().add(TAG, model, firebaseManager.getFirebaseAuth().getCurrentUser().getUid())
                .addOnSuccessListener(documentReference -> {
                    currentModel.setId(documentReference.getId());
                    idSelectedImages.add(dbHelper.insert("Image", currentModel));
                    update("id", documentReference.getId(), "id = '" + documentReference.getId() + "'");
                    dbHelper.close();
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
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
        firebaseManager.getFirebaseHelper().update(TAG, data, id, firebaseManager.getFirebaseAuth().getCurrentUser().getUid())
                .continueWith(task -> {
                    if (task.isSuccessful()) {
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

    public String getIdByRef(String ref) {
        if (currentModel.getRef().equals(ref))
            return currentModel.getId();
        else
            return dbHelper.getId("Image", "ref = '" + ref + "'");
    }

    public String getImageRefById(String imageId) {
        return dbHelper.getImageRefById(imageId);
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
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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
            return imageRef.getDownloadUrl();
        });
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
                                                }
                                            });
                                }
                            }
                        }
                    }
                    return imageIdsInFirestore;
                });
    }

    public boolean toggleFavoriteImage(String imageId) {
        boolean isFavourited = getImageById(imageId).getIs_favourited();

        update("is_favourited", isFavourited ? "0" : "1", "id = '" + imageId + "'");
        return !isFavourited;
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
        filename = firebaseManager.getFirebaseAuth().getCurrentUser().getUid() + "/" + filename;
        return filename;
    }


    public void restoreSelectedImage(String imageURL) {
        String imageID = dbHelper.getImageIdByURL(imageURL);
        dbHelper.setDelete(imageID, false);

        Intent intent = new Intent(activity, MainFragmentController.class);
        intent.putExtra("fragmentToLoad", "Bin");
        activity.startActivity(intent);
        activity.finish();
    }

    public long getTotalCapacityFromImageIDs(List<String> imageIDs) {
        return dbHelper.getTotalCapacityFromImageIDs(imageIDs);
    }

    public void setDelete(String imageId, boolean isDelete) {
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

    public List<String> selectImagesByNotice(String notice) {
        return dbHelper.selectImagesByNotice(notice);
    }

    public List<String> selectImageNamesByNotice(String notice) {
        return dbHelper.selectImageNamesByNotice(notice);
    }
}
