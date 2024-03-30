package com.example.albumgallery.view.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class EditImageActivity extends AppCompatActivity {
    private MainController mainController;
    private ImageView memeImageView;
    private float scaleFactor = 1.0f;
    private GestureDetectorCompat gestureDetector;
    private float startX, startY, imageX, imageY;

    private static final int CROP_IMAGE_REQUEST_CODE = 100;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);
        mainController = new MainController(this);

        ImageView backButton = findViewById(R.id.backButton);
        memeImageView = findViewById(R.id.memeImageView);

        backButton.setOnClickListener(v -> {
            showSaveChangesDialog();
        });

        ImageView zoomInButton = findViewById(R.id.zoomInButton);
        ImageView zoomOutButton = findViewById(R.id.zoomOutButton);
        ImageView rotateButton = findViewById(R.id.rotateButton);
        ImageView cutButton = findViewById(R.id.cutButton);

        memeImageView.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    imageX = memeImageView.getX();
                    imageY = memeImageView.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = event.getX() - startX;
                    float dy = event.getY() - startY;
                    memeImageView.setX(imageX + dx);
                    memeImageView.setY(imageY + dy);
                    break;
            }
            return true;
        });

        gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // Kiểm tra xem cử chỉ diễn ra trong phạm vi ImageView
                if (isMotionEventInsideView(e2.getRawX(), e2.getRawY(), memeImageView)) {
                    // Thực hiện zoom
                    scaleFactor -= distanceY / 1000;
                    scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 3.0f));
                    memeImageView.setScaleX(scaleFactor);
                    memeImageView.setScaleY(scaleFactor);
                    return true;
                }
                return false;
            }
        });


        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi nút zoom in được bấm, tăng scaleFactor và cập nhật ảnh
                scaleFactor += 0.1f;
                scaleFactor = Math.min(scaleFactor, 3.0f);
                memeImageView.setScaleX(scaleFactor);
                memeImageView.setScaleY(scaleFactor);
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi nút zoom out được bấm, giảm scaleFactor và cập nhật ảnh
                scaleFactor -= 0.1f;
                scaleFactor = Math.max(0.1f, scaleFactor);
                memeImageView.setScaleX(scaleFactor);
                memeImageView.setScaleY(scaleFactor);
            }
        });

        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xoay ảnh 90 độ theo chiều kim đồng hồ
                memeImageView.setRotation(memeImageView.getRotation() + 90);
            }
        });

        cutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageCropActivity();
            }
        });
        Bitmap croppedImage = getIntent().getParcelableExtra("croppedImage");

        // Kiểm tra xem hình ảnh đã cắt có tồn tại hay không
        if (croppedImage != null) {

            ImageView imageView = findViewById(R.id.memeImageView);
            imageView.setImageBitmap(croppedImage);
        } else {

            long id = getIntent().getLongExtra("id", 0);
            String imageURL = mainController.getImageController().getImageById(id).getRef();
            Glide.with(this).load(Uri.parse(imageURL)).into(memeImageView);
        }
    }

    @Override
    public void onBackPressed() {
        showSaveChangesDialog();
    }

    private boolean isMotionEventInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        // Kiểm tra xem tọa độ x, y có nằm trong View không
        return (x > viewX && x < (viewX + view.getWidth()) && y > viewY && y < (viewY + view.getHeight()));
    }

    private void showSaveChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Changes");
        builder.setMessage("Do you want to save changes before going back?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save changes here
                saveChangesAndGoBack();
            }
        });
        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Discard changes and go back
                goBackToDetailScreen();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing, just dismiss the dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void saveChangesAndGoBack() {
        goBackToDetailScreen();
    }

    private void goBackToDetailScreen() {
        finish();
    }

    private void startImageCropActivity() {
        Intent intent = new Intent(EditImageActivity.this, CropImageActivity.class);
        intent.putExtra("imageUri", getImageUri());
        startActivityForResult(intent, CROP_IMAGE_REQUEST_CODE);
    }

    private Uri getImageUri() {
        // Lấy Bitmap từ ImageView
        Bitmap bitmap = ((BitmapDrawable) memeImageView.getDrawable()).getBitmap();

        // Tạo một file tạm thời để lưu ảnh
        File tempFile = null;
        try {
            tempFile = File.createTempFile("temp_image", ".jpg", getCacheDir());
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Trả về Uri của file tạm thời
        return tempFile != null ? Uri.fromFile(tempFile) : null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CROP_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bitmap croppedImage = data.getParcelableExtra("croppedImage");
            if (croppedImage != null) {
                memeImageView.setImageBitmap(croppedImage);
            }
        }
    }

}