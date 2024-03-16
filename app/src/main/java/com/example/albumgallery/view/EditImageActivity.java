package com.example.albumgallery.view;

import com.bumptech.glide.Glide;
import com.example.albumgallery.R;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GestureDetectorCompat;
import androidx.core.widget.NestedScrollView;

import android.view.GestureDetector;

import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import android.graphics.Matrix;


public class EditImageActivity extends AppCompatActivity {
    private ImageView memeImageView;
    private float scaleFactor = 1.0f;
    private GestureDetectorCompat gestureDetector;
    private float startX, startY, imageX, imageY;
    private static final int REQUEST_IMAGE_CROP = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        ImageView backButton = findViewById(R.id.backButton);
        memeImageView = findViewById(R.id.memeImageView);
        NestedScrollView memeScrollView = findViewById(R.id.memeScrollView);

        backButton.setOnClickListener(v -> {
            showSaveChangesDialog();
        });

        ImageView zoomInButton = findViewById(R.id.zoomInButton);
        ImageView zoomOutButton = findViewById(R.id.zoomOutButton);
        ImageView rotateButton = findViewById(R.id.rotateButton);
        ImageView cutButton = findViewById(R.id.cutButton);

        memeImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
            }
        });

        gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // Kiểm tra xem cử chỉ diễn ra trong phạm vi ImageView
                if (isMotionEventInsideView(e2.getRawX(), e2.getRawY(), memeImageView)) {
                    // Thực hiện zoom
                    scaleFactor -= distanceY / 1000;
                    scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 3.0f)); // Giới hạn scaleFactor
                    memeImageView.setScaleX(scaleFactor);
                    memeImageView.setScaleY(scaleFactor);
                    return true;
                }
                return false;
            }
        });

//        memeScrollView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                gestureDetector.onTouchEvent(event);
//                return true;
//            }
//        });

        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi nút zoom in được bấm, tăng scaleFactor và cập nhật ảnh
                scaleFactor += 0.1f;
                scaleFactor = Math.min(scaleFactor, 3.0f); // Giới hạn scaleFactor
                memeImageView.setScaleX(scaleFactor);
                memeImageView.setScaleY(scaleFactor);
            }
        });

        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi nút zoom out được bấm, giảm scaleFactor và cập nhật ảnh
                scaleFactor -= 0.1f;
                scaleFactor = Math.max(0.1f, scaleFactor); // Giới hạn scaleFactor
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

        // lấy ảnh từ detail image activity sang edit image activity
        String imagePath = getIntent().getStringExtra("imagePath");
        ImageView memeImageView = findViewById(R.id.memeImageView);
        Glide.with(this).load(imagePath).into(memeImageView);


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
        // save picture
        goBackToDetailScreen();
    }

    private void goBackToDetailScreen() {
        Intent intent = new Intent(EditImageActivity.this, DetailPicture.class);
        intent.putExtra("imagePath", getIntent().getStringExtra("imagePath"));
        startActivity(intent);
        finish();
    }

    private void startImageCropActivity() {
        // Khởi tạo một intent để mở hoạt động cắt ảnh
        Intent intent = new Intent(EditImageActivity.this, CropImageActivity.class);
        intent.setData(Uri.parse(getIntent().getStringExtra("imagePath"))); // Chuyển đổi đường dẫn sang Uri
        startActivityForResult(intent, REQUEST_IMAGE_CROP);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CROP && resultCode == RESULT_OK) {
            // Xử lý ảnh đã cắt được ở đây
            if (data != null) {
                String croppedImagePath = data.getStringExtra("croppedImagePath");
                // Load ảnh cắt được vào ImageView
                Glide.with(this).load(croppedImagePath).into(memeImageView);
            }
        }
    }

}
