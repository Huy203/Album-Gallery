package com.example.albumgallery.view.activity;

import static com.example.albumgallery.utils.Constant.REQUEST_CODE_EDIT_IMAGE;
import static com.example.albumgallery.utils.Utilities.byteArrayToBitmap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.bumptech.glide.Glide;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class EditImageActivity extends AppCompatActivity {
    private MainController mainController;
    private ImageView mImageView;
    private float scaleFactor = 1.0f;
    private GestureDetectorCompat gestureDetector;
    private float startX, startY, imageX, imageY;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        mainController = new MainController(this);
        mImageView = findViewById(R.id.imageView);

        appBarAction();

        mImageView.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    imageX = mImageView.getX();
                    imageY = mImageView.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = event.getX() - startX;
                    float dy = event.getY() - startY;
                    mImageView.setX(imageX + dx);
                    mImageView.setY(imageY + dy);
                    break;
            }
            return true;
        });

        gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // Kiểm tra xem cử chỉ diễn ra trong phạm vi ImageView
                if (isMotionEventInsideView(e2.getRawX(), e2.getRawY(), mImageView)) {
                    // Thực hiện zoom
                    scaleFactor -= distanceY / 1000;
                    scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 3.0f));
                    mImageView.setScaleX(scaleFactor);
                    mImageView.setScaleY(scaleFactor);
                    return true;
                }
                return false;
            }
        });

        Bitmap croppedImage = getIntent().getParcelableExtra("croppedImage");

        // Kiểm tra xem hình ảnh đã cắt có tồn tại hay không
        if (croppedImage != null) {
            mImageView = findViewById(R.id.imageView);
            mImageView.setImageBitmap(croppedImage);
        } else {
            String id = getIntent().getStringExtra("id");
            String imageURL = mainController.getImageController().getImageById(id).getRef();
            Glide.with(this).load(Uri.parse(imageURL)).into(mImageView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageView.setScaleX(scaleFactor);
        mImageView.setScaleY(scaleFactor);
        //Toast.makeText(this, "Double tap to zoom in/out", Toast.LENGTH_SHORT).show();
    }

    private void appBarAction() {
        int[] buttonIds = {R.id.action_zoom_in, R.id.action_zoom_out, R.id.action_rotate, R.id.action_crop, R.id.action_beautify,  R.id.action_back};

        for (int buttonId : buttonIds) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(v -> {
                if (buttonId == buttonIds[0]) {
                    scaleFactor += 0.1f;
                    scaleFactor = Math.min(scaleFactor, 3.0f);
                    mImageView.setScaleX(scaleFactor);
                    mImageView.setScaleY(scaleFactor);
                } else if (buttonId == buttonIds[1]) {
                    scaleFactor -= 0.1f;
                    scaleFactor = Math.max(0.1f, scaleFactor);
                    mImageView.setScaleX(scaleFactor);
                    mImageView.setScaleY(scaleFactor);
                } else if (buttonId == buttonIds[2]) {
                    mImageView.setRotation(mImageView.getRotation() + 90);
                } else if (buttonId == buttonIds[3]) {
                    startImageCropActivity();
                } else if (buttonId == buttonIds[4]) {
                    startImageBeautyActivity();
                } else if (buttonId == buttonIds[5]) {
                    Intent intent = new Intent();
                    intent.putExtra("update", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    private boolean isMotionEventInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        // Kiểm tra xem tọa độ x, y có nằm trong View không
        return (x > viewX && x < (viewX + view.getWidth()) && y > viewY && y < (viewY + view.getHeight()));
    }

//    private void showSaveChangesDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Save Changes");
//        builder.setMessage("Do you want to save changes before going back?");
//        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // Save changes here
//                saveChangesAndGoBack();
//            }
//        });
//        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // Discard changes and go back
//                goBackToDetailScreen();
//            }
//        });
//        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // Do nothing, just dismiss the dialog
//                dialog.dismiss();
//            }
//        });
//        builder.show();
//    }

//    private void saveChangesAndGoBack() {
//        goBackToDetailScreen();
//    }

    private void startImageCropActivity() {
        Intent intent = new Intent(EditImageActivity.this, CropImageActivity.class);
        String id = getIntent().getStringExtra("id");
        Log.v("EditImageActivity", "id: " + id);
        intent.putExtra("id", id);
        startActivityForResult(intent, 100);
    }

    private void startImageBeautyActivity() {
        Intent intent = new Intent(EditImageActivity.this, BeautyImageActivity.class);
        long id = getIntent().getLongExtra("id", -1);
        intent.putExtra("id", id);
        startActivityForResult(intent, 100);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("EditImageActivity", "onActivityResult");
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            try {
                byte[] byteArray = data.getByteArrayExtra("imageByteArray");
                Bitmap croppedImage = byteArrayToBitmap(byteArray);
                if (croppedImage != null) {
                    mImageView.setImageBitmap(croppedImage);
                    mainController.getImageController().onActivityResult(REQUEST_CODE_EDIT_IMAGE, RESULT_OK, data);
                }
            } catch (Exception e) {
                Log.e("EditImageActivity", "Error loading image: " + e.getMessage());
            }
        }
    }
}