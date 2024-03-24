package com.example.albumgallery.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.model.ImageModel;

public class DetailPicture extends AppCompatActivity {
    private MainController mainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image);

        mainController = new MainController(this);

        ImageView backButton = findViewById(R.id.backButton);
        ImageView pencilButton = findViewById(R.id.pencilButton);
        ImageView ellipsisButton = findViewById(R.id.ellipsisButton);
        ImageView trashButton = findViewById(R.id.trashButton);
        Button ImageInfo = findViewById(R.id.ImageInfo);

        pencilButton.setOnClickListener(v -> {
            Intent intent = new Intent(DetailPicture.this, EditImageActivity.class);
            // truyền ảnh sang edit image activity
            String imageURL = getIntent().getStringExtra("imageURL");
            long id = mainController.getImageController().getIdByRef(imageURL);
            intent.putExtra("id", id);
            startActivity(intent);
            finish();
        });
        backButton.setOnClickListener(v -> {
//            Intent intent = new Intent(DetailPicture.this, HomeScreen.class);
//            startActivity(intent);
            supportFinishAfterTransition();
        });

        ellipsisButton.setOnClickListener(v -> {
            // Hiển thị menu hoặc dialog chọn tùy chọn
            showOptionsDialog();
        });

        trashButton.setOnClickListener(v -> {
            // Get the current image URL from the intent extras
            String imageURL = getIntent().getStringExtra("imageURL");

            if (imageURL != null) {
                // Call deleteSelectedImage() method from ImageController
                // mainController.getImageController().deleteSelectedImage(imageURL, 0);
                showDeleteConfirmationDialog(imageURL);
            } else {
                Toast.makeText(this, "No image to delete", Toast.LENGTH_SHORT).show();
            }
        });

        ImageInfo.setOnClickListener(v -> {
            String imageURL = getIntent().getStringExtra("imageURL");
            if (imageURL != null) {
                long id = mainController.getImageController().getIdByRef(imageURL);
                ImageModel imageModel = mainController.getImageController().getImageById(id);
                View view = LayoutInflater.from(this).inflate(R.layout.image_info, null);
                this.setContentView(view);



            }
        });

        // Lấy ảnh từ image adapter, hiển thị vào edit image screen.
        String imageURL = getIntent().getStringExtra("imageURL");
        ImageView imageView = findViewById(R.id.memeImageView);
        Glide.with(this).load(Uri.parse(imageURL)).into(imageView);

    }

    private void showDeleteConfirmationDialog(String imageURL) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this image?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call deleteSelectedImage() method from ImageController
                mainController.getImageController().deleteSelectedImage(imageURL);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
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

