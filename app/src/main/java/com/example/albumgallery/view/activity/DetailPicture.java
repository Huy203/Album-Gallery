package com.example.albumgallery.view.activity;
import com.bumptech.glide.Glide;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.albumgallery.controller.OnSwipeTouchListener;

import java.util.List;

public class DetailPicture extends AppCompatActivity {
    private MainController mainController;
    private ImageView imageView;
    private List<String> imagePaths;
    private int currentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image);

        mainController = new MainController(this);
        imagePaths = mainController.getImagePaths();
        currentPosition = getIntent().getIntExtra("position", 0);

        imageView = findViewById(R.id.memeImageView);
        loadImage(currentPosition);

        // Detect swipe gestures
        imageView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                // Swipe left, show next image
                if (currentPosition < imagePaths.size() - 1) {
                    currentPosition++;
                    loadImage(currentPosition);
                }
            }

            @Override
            public void onSwipeRight() {
                // Swipe right, show previous image
                if (currentPosition > 0) {
                    currentPosition--;
                    loadImage(currentPosition);
                }
            }
        });

        ImageView backButton = findViewById(R.id.backButton);
        ImageView pencilButton = findViewById(R.id.pencilButton);
        ImageView ellipsisButton = findViewById(R.id.ellipsisButton);
        ImageView trashButton = findViewById(R.id.trashButton);

        pencilButton.setOnClickListener(v -> {
            Intent intent = new Intent(DetailPicture.this, EditImageActivity.class);
            // truyền ảnh sang edit image activity
            String imagePath = imagePaths.get(currentPosition);
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

        trashButton.setOnClickListener(v -> {
            // Get the current image URL from the intent extras
            String longImageURL = getIntent().getStringExtra("imagePath");

            String imageURL = mainController.getImageController().checkExistURL(longImageURL);

            if (imageURL != null) {
                // Call deleteSelectedImage() method from ImageController
                mainController.getImageController().deleteSelectedImage(imageURL);
            } else {
                Toast.makeText(this, "No image to delete", Toast.LENGTH_SHORT).show();
            }
        });

//        // Lấy ảnh từ image adapter, hiển thị vào edit image screen.
//        String imagePath = getIntent().getStringExtra("imagePath");
//        ImageView imageView = findViewById(R.id.memeImageView);
//        Glide.with(this).load(Uri.parse(imagePath)).into(imageView);

    }

    private void loadImage(int position) {
        Glide.with(this).load(Uri.parse(imagePaths.get(position))).into(imageView);
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

