package com.example.albumgallery.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import com.example.albumgallery.R;


public class CropImageActivity extends AppCompatActivity {
    private CropImageView cropImageView;
    private Spinner aspectRatioSpinner;
    private boolean check = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        cropImageView = findViewById(R.id.cropImageView);
        aspectRatioSpinner = findViewById(R.id.aspectRatioSpinner);

        Uri imageUri = getIntent().getParcelableExtra("imageUri");

        if (imageUri != null) {
            cropImageView.setImageUriAsync(imageUri);
        }

        setupAspectRatioSpinner();
        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditActivity(true);
            }
        });

        ImageView buttonBack = findViewById(R.id.backButton);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAcceptChangesDialog();
            }
        });

        Button buttonCrop = findViewById(R.id.buttonCrop);
        buttonCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check = true;
                Bitmap croppedImage = cropImageView.getCroppedImage();
                cropImageView.setImageBitmap(croppedImage);
            }
        });
    }

    private void setupAspectRatioSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.aspect_ratios, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aspectRatioSpinner.setAdapter(adapter);

        aspectRatioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedRatio = (String) parentView.getItemAtPosition(position);
                setCropAspectRatio(selectedRatio);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
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

    @Override
    public void onBackPressed() {
        showAcceptChangesDialog();
    }


    private void showAcceptChangesDialog() {
        if(check){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Accept Changes");
            builder.setMessage("Do you want to accept changes?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Chấp nhận thay đổi và trở về màn hình chỉnh sửa với hình đã cắt
                    goToEditActivity(true);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goToEditActivity(false);
                }
            });
            builder.show();
        }else{
            goToEditActivity(false);
        }

    }

    private void goToEditActivity(boolean acceptChanges) {
        Intent intent = new Intent();
        if (acceptChanges) {
            Bitmap croppedImage = cropImageView.getCroppedImage();
            intent.putExtra("croppedImage", croppedImage);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }



}