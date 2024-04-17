package com.example.albumgallery.helper;

import com.example.albumgallery.model.Model;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class FirebaseHelper {
    private final FirebaseFirestore db;

    public FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
    }

    // Method to add a new image to Firestore
    public Task<DocumentReference> add(String collection, Model model, String userId) {
        CollectionReference imagesRef = db.collection("App").document(userId).collection(collection);
        return imagesRef.add(model);
    }

    public Task<Void> addDocument(String collection, Model model) {
        CollectionReference collectionReference = db.collection("App");
        return collectionReference.document(model.getId()).set(model);

    }

    // Method to retrieve all images from Firestore
    public Task<QuerySnapshot> getAll(String userId, String collection) {
        CollectionReference collectionRef = db.collection("App").document(userId).collection(collection);
        return collectionRef.get();
    }

    // Method to update an existing image in Firestore
    public Task<Void> update(String collection, Map<String, Object> data, String documentId, String userId) {
        if (collection.equals("User")) {
            DocumentReference documentRef = db.collection("App").document(userId);
            return documentRef.update(data);
        } else {
            DocumentReference documentRef = db.collection("App").document(userId).collection(collection).document(documentId);
            return documentRef.update(data);
        }
    }

    // Method to delete an image from Firestore
    public Task<Void> deleteImage(String collection, String documentId, String userId) {
        DocumentReference imageRef = db.collection("App").document(userId).collection(collection).document(documentId);
        return imageRef.delete();
    }

    // Method to get document ID by reference value
    public Task<String> getDocumentIdByRef(String collection, String refValue) {
        CollectionReference imagesRef = db.collection(collection);
        Query query = imagesRef.whereEqualTo("ref", refValue).limit(1);
        return query.get().continueWith(task -> {
            QuerySnapshot querySnapshot = task.getResult();
            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                return querySnapshot.getDocuments().get(0).getId();
            } else {
                return null;
            }
        });
    }


    public Task<DocumentSnapshot> getById(String collection, String documentId, String userId) {
        // Get a reference to the document
        if (collection.equals("User")) {
            DocumentReference docRef = db.collection("App").document(userId);
            // Fetch the document
            return docRef.get();
        } else {
            DocumentReference docRef = db.collection("App").document(userId).collection(collection).document(documentId);
            // Fetch the document
            return docRef.get();
        }
    }

    public Task<QuerySnapshot> getAll(String collection) {
        CollectionReference imagesRef = db.collection(collection);
        return imagesRef.get();
    }
}
