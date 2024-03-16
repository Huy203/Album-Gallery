package com.example.albumgallery.view.activity;
import com.example.albumgallery.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.core.widget.NestedScrollView;
import android.view.GestureDetector;


public class EditImageActivity extends AppCompatActivity {
    private ImageView memeImageView;
    private float scaleFactor = 1.0f;
    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        ImageView backButton = findViewById(R.id.backButton);
        memeImageView = findViewById(R.id.memeImageView);
        NestedScrollView memeScrollView = findViewById(R.id.memeScrollView);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(EditImageActivity.this, DetailPicture.class);
            startActivity(intent);
            finish();
        });

        ImageView zoomInButton = findViewById(R.id.zoomInButton);
        ImageView zoomOutButton = findViewById(R.id.zoomOutButton);
        ImageView rotateButton = findViewById(R.id.rotateButton);

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

        memeScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

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
    }
    private boolean isMotionEventInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];
        // Kiểm tra xem tọa độ x, y có nằm trong View không
        return (x > viewX && x < (viewX + view.getWidth()) && y > viewY && y < (viewY + view.getHeight()));
    }

}
