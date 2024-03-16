package com.example.albumgallery.view;
import com.bumptech.glide.Glide;
import com.example.albumgallery.R;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class DetailPicture extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image);

        ImageView backButton = findViewById(R.id.backButton);
        ImageView pencilButton = findViewById(R.id.pencilButton);
        ImageView ellipsisButton = findViewById(R.id.ellipsisButton);

        pencilButton.setOnClickListener(v -> {
            Intent intent = new Intent(DetailPicture.this, EditImageActivity.class);
            // truyền ảnh sang edit image activity
            String imagePath = getIntent().getStringExtra("imagePath");
            intent.putExtra("imagePath", imagePath);
            startActivity(intent);
            finish();
        });
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(DetailPicture.this, HomeScreen.class);
            startActivity(intent);
            finish();
        });

        ellipsisButton.setOnClickListener(v -> {
            // Hiển thị menu hoặc dialog chọn tùy chọn
            showOptionsDialog();
        });

        // Lấy ảnh từ image adapter, hiển thị vào edit image screen.
        String imagePath = getIntent().getStringExtra("imagePath");
        ImageView imageView = findViewById(R.id.memeImageView);
        Glide.with(this).load(Uri.parse(imagePath)).into(imageView);

    }

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(new CharSequence[]{"Add to album", "Mat", "Start referencing", "Detail"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý khi người dùng chọn một tùy chọn
                switch (which) {
                    case 0:
                        // Xử lý khi người dùng chọn "Add to album"
                        break;
                    case 1:
                        // Xử lý khi người dùng chọn "Mat"
                        break;
                    case 2:
                        // Xử lý khi người dùng chọn "Start referencing"
                        break;
                    case 3:
                        // Xử lý khi người dùng chọn "Detail"
                        break;
                }
            }
        });
        builder.show();
    }

}

