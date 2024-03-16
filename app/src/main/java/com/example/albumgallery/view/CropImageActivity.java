package com.example.albumgallery.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.albumgallery.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CropImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button cropButton;

    private String imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        imageView = findViewById(R.id.imageView);
        cropButton = findViewById(R.id.cropButton);

        // Lấy Uri của ảnh từ Intent
        Uri imageUri = getIntent().getData();

        // Hiển thị ảnh trên ImageView
        if (imageUri != null) {
            imageView.setImageURI(imageUri);
        }

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Thực hiện logic cắt ảnh ở đây và lưu ảnh đã cắt
                performImageCrop();
            }
        });
    }

    private void performImageCrop() {
        // Logic cắt ảnh ở đây
        // Ví dụ: Cắt ảnh thành 300x300 pixels
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, 300, 300);
        saveCroppedImage(croppedBitmap);
    }

    private void saveCroppedImage(Bitmap croppedBitmap) {
        // Tạo đường dẫn lưu ảnh đã cắt
        String croppedImagePath = Environment.getExternalStorageDirectory().getPath() + "/cropped_image.jpg";
        File croppedImageFile = new File(croppedImagePath);

        try {
            OutputStream outputStream = new FileOutputStream(croppedImageFile);
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("croppedImagePath", croppedImagePath);
            setResult(RESULT_OK, resultIntent);

            finish();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
