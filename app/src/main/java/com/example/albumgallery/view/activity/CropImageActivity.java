package com.example.albumgallery.view.activity;

import static com.example.albumgallery.utils.Utilities.bitmapToByteArray;
import static com.example.albumgallery.utils.Utilities.convertFromUriToBitmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.canhub.cropper.CropImageView;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;

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

        String id = getIntent().getStringExtra("id");
        String imageURL = mainController.getImageController().getImageById(id).getRef();
        Glide.with(this)
                .asBitmap()
                .load(Uri.parse(imageURL))
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        cropImageView.setImageBitmap(resource);
                        return false;
                    }
                })
                .submit();

        setupAspectRatioSpinner();
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

    public void cropAction(View view) {
        hasChanges = true;
        Bitmap croppedImage = cropImageView.getCroppedImage();
        cropImageView.setImageBitmap(croppedImage);
    }

    public void saveAction(View view) {
        Bitmap croppedImage = cropImageView.getCroppedImage();

        Intent intent = new Intent(this, EditImageActivity.class);
        intent.putExtra("imageByteArray", bitmapToByteArray(croppedImage));
        setResult(RESULT_OK, intent);
        finish();
    }

    public void backAction(View view) {
        finish();
    }
}