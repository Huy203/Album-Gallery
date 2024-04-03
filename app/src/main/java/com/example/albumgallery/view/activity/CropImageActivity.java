package com.example.albumgallery.view.activity;

import static com.example.albumgallery.utils.Utilities.bitmapToByteArray;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CropImageActivity extends AppCompatActivity {
    private MainController mainController;
    private CropImageView cropImageView;
    private Spinner aspectRatioSpinner;
    private boolean hasChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        cropImageView = findViewById(R.id.cropImageView);
        aspectRatioSpinner = findViewById(R.id.aspectRatioSpinner);

        mainController = new MainController(this);

//        byte[] imageByteArray = getIntent().getByteArrayExtra("imageByteArray");
//        if (imageByteArray != null) {
//            cropImageView.setImageBitmap(byteArrayToBitmap(imageByteArray));
//        }
        long id = getIntent().getLongExtra("id", -1);
        String imageURL = mainController.getImageController().getImageById(id).getRef();
        try {
            Glide.with(this)
                    .asBitmap()
                    .load(imageURL)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                            cropImageView.setImageBitmap(resource);
                        }
                    });
        } catch (Exception e) {
            Log.e("CropImageActivity", "Error loading image: " + e.getMessage());
        }

        setupAspectRatioSpinner();

        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(v -> goBack(true));

        ImageView buttonBack = findViewById(R.id.backButton);
        buttonBack.setOnClickListener(v -> goBack(false));

        Button buttonCrop = findViewById(R.id.buttonCrop);
        buttonCrop.setOnClickListener(v -> {
            hasChanges = true;
            Bitmap croppedImage = cropImageView.getCroppedImage();
            cropImageView.setImageBitmap(croppedImage);
        });
    }

    private void setupAspectRatioSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.aspect_ratios, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aspectRatioSpinner.setAdapter(adapter);

        aspectRatioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRatio = (String) parent.getItemAtPosition(position);
                Log.v("CropImageActivity", "Selected ratio: " + selectedRatio);
                setCropAspectRatio(selectedRatio);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setCropAspectRatio(String ratio) {
        switch (ratio) {
            case "1:1":
                cropImageView.setAspectRatio(1, 1);
                break;
            case "4:3":
                cropImageView.setAspectRatio(4, 3);
                break;
            case "Original":
                cropImageView.clearAspectRatio();
                break;
            case "3:2":
                cropImageView.setAspectRatio(3, 2);
                break;
            case "16:9":
                cropImageView.setAspectRatio(16, 9);
                break;
        }
    }

//    private void showAcceptChangesDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Accept Changes");
//        builder.setMessage("Do you want to accept changes?");
//        builder.setPositiveButton("Yes", (dialog, which) -> goBack(true));
//        builder.setNegativeButton("No", (dialog, which) -> goBack(false));
//        if (hasChanges) {
//            builder.show();
//        } else {
//            goBack(false);
//        }
//    }

    private void goBack(boolean acceptChanges) {
        if (acceptChanges) {
            Bitmap croppedImage = cropImageView.getCroppedImage();

            Intent intent = new Intent(this, EditImageActivity.class);
            intent.putExtra("imageByteArray", bitmapToByteArray(croppedImage));
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
