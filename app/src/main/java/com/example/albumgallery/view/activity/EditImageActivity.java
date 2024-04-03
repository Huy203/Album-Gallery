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

        ImageView backButton = findViewById(R.id.backButton);
        mImageView = findViewById(R.id.imageView);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("update", true);
            setResult(RESULT_OK, intent);
            finish();
        });

        ImageView zoomInButton = findViewById(R.id.zoomInButton);
        ImageView zoomOutButton = findViewById(R.id.zoomOutButton);
        ImageView rotateButton = findViewById(R.id.rotateButton);
        ImageView cutButton = findViewById(R.id.cutButton);

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


        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi nút zoom in được bấm, tăng scaleFactor và cập nhật ảnh
                scaleFactor += 0.1f;
                scaleFactor = Math.min(scaleFactor, 3.0f);
                mImageView.setScaleX(scaleFactor);
                mImageView.setScaleY(scaleFactor);
            }
        });

        zoomOutButton.setOnClickListener(v -> {
            // Khi nút zoom out được bấm, giảm scaleFactor và cập nhật ảnh
            scaleFactor -= 0.1f;
            scaleFactor = Math.max(0.1f, scaleFactor);
            mImageView.setScaleX(scaleFactor);
            mImageView.setScaleY(scaleFactor);
        });

        rotateButton.setOnClickListener(v -> {
            // Xoay ảnh 90 độ theo chiều kim đồng hồ
            mImageView.setRotation(mImageView.getRotation() + 90);
        });

        cutButton.setOnClickListener(v -> startImageCropActivity());
        Bitmap croppedImage = getIntent().getParcelableExtra("croppedImage");

        // Kiểm tra xem hình ảnh đã cắt có tồn tại hay không
        if (croppedImage != null) {
            ImageView imageView = findViewById(R.id.imageView);
            imageView.setImageBitmap(croppedImage);
        } else {
            long id = getIntent().getLongExtra("id", 0);
            String imageURL = mainController.getImageController().getImageById(id).getRef();
            Glide.with(this).load(Uri.parse(imageURL)).into(mImageView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageView.setScaleX(scaleFactor);
        mImageView.setScaleY(scaleFactor);
        Toast.makeText(this, "Double tap to zoom in/out", Toast.LENGTH_SHORT).show();
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

    private void goBackToDetailScreen() {
        finish();
    }

    private void startImageCropActivity() {
        Intent intent = new Intent(EditImageActivity.this, CropImageActivity.class);
        long id = getIntent().getLongExtra("id", -1);
        intent.putExtra("id", id);
//        Uri imageUri = Uri.parse(mainController.getImageController().getImageById(id).getRef());
//        byte[] byteArray = bitmapToByteArray(uriToBitmap(imageUri, (Context)this));
//        intent.putExtra("imageByteArray", byteArray);
        startActivityForResult(intent, 100);
    }


    private Uri getImageUri() {
        // Lấy Bitmap từ ImageView
        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();

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