package com.example.albumgallery.utils.textRecognization;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.albumgallery.view.listeners.TextRecognitionListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TextRecognization extends AsyncTask<String, Void, String> {
    private TextRecognitionListener listener;
    private Bitmap bitmap;
    private List<Rect> boundingBoxes;
    private List<String> textRecognized;

    public TextRecognization(Context context) {
        this.listener = (TextRecognitionListener) context;
        this.textRecognized = new ArrayList<>();
        this.boundingBoxes = new ArrayList<>();
        this.bitmap = null;
    }

    @Override
    protected String doInBackground(String... urls) {
        String uri = urls[0];
        Bitmap bitmap = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            if (bitmap != null) {
                this.bitmap = bitmap;
                recognizeText(bitmap);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void recognizeText(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        Log.v("Image Size", "Sample size: " + image.getBitmap().getWidth() + "x" + image.getBitmap().getHeight());
        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                String resultText = firebaseVisionText.getText();
                                Log.v("TextRecognization", "Text: " + resultText);
                                for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                                    String blockText = block.getText();
                                    Float blockConfidence = block.getConfidence();
                                    List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
                                    Point[] blockCornerPoints = block.getCornerPoints();
                                    Rect blockFrame = block.getBoundingBox();
                                    for (FirebaseVisionText.Line line : block.getLines()) {
                                        String lineText = line.getText();
                                        Float lineConfidence = line.getConfidence();
                                        List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
                                        Point[] lineCornerPoints = line.getCornerPoints();
                                        Rect lineFrame = line.getBoundingBox();

                                        textRecognized.add(lineText);
                                        boundingBoxes.add(lineFrame);
                                        Log.v("TextRecognization", "Line: " + lineText);
                                        Log.v("TextRecognization", "Line Confidence: " + lineConfidence);
                                        Log.v("TextRecognization", "Line Frame: " + lineFrame);
                                    }
                                }
                                if (listener != null) {
                                    listener.onTextRecognized(textRecognized, boundingBoxes, bitmap);
                                }
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}