package com.example.albumgallery.view.activity;

import static com.example.albumgallery.utils.Constant.REQUEST_CODE_EDIT_IMAGE;

import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.view.fragment.ImageInfo;
import com.example.albumgallery.view.listeners.ImageInfoListener;
import com.example.albumgallery.view.listeners.OnSwipeTouchListener;

import java.io.IOException;
import java.util.List;

public class DetailPicture extends AppCompatActivity implements ImageInfoListener {

    private MainController mainController;
    private ImageView imageView;
    private List<String> imagePaths;
    private TextView qrLink;
    private int currentPosition;
    private View imageInfoView;
    private ImageInfo imageInfoFragment;
    private boolean isImageInfoVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_image);

        initializeViews();
        setupListeners();
        loadImage(currentPosition);
        loadImageInfo();
        loadQRCodeLink();
    }

    private void initializeViews() {
        mainController = new MainController(this);
        imagePaths = mainController.getImagePaths();
        currentPosition = getIntent().getIntExtra("position", 0);

        imageView = findViewById(R.id.memeImageView);
        qrLink = findViewById(R.id.qrLink);
        imageInfoView = findViewById(R.id.imageInfo);
        imageInfoFragment = new ImageInfo();
    }

    private void setupListeners() {
        // Swipe gestures
        imageView.setOnTouchListener(new OnSwipeTouchListener(this, imageView) {
            @Override
            public void onSwipeLeft() {
                if (currentPosition < imagePaths.size() - 1) {
                    currentPosition++;
                    loadImage(currentPosition);
                }
            }

            @Override
            public void onSwipeRight() {
                if (currentPosition > 0) {
                    currentPosition--;
                    loadImage(currentPosition);
                }
            }
        });

        // Buttons
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("updateUI", false);
            setResult(RESULT_OK, intent);
            supportFinishAfterTransition();
        });
        findViewById(R.id.pencilButton).setOnClickListener(v -> launchEditImageActivity());
        findViewById(R.id.ellipsisButton).setOnClickListener(v -> showOptionsDialog());
        findViewById(R.id.trashButton).setOnClickListener(v -> deleteImage());
        findViewById(R.id.ImageInfo).setOnClickListener(v -> toggleImageInfo());
    }

    private void setAsWallpaper(String imagePath) {
        Glide.with(this)
                .asBitmap()
                .load(Uri.parse(imagePath))
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e("DetailPicture", "Failed to load image: " + e.getMessage());
                        Toast.makeText(DetailPicture.this, "Failed to set wallpaper", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                        try {
                            wallpaperManager.setBitmap(resource);
                            //Toast.makeText(DetailPicture.this, "Wallpaper set successfully", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Log.e("DetailPicture", "Failed to set wallpaper: " + e.getMessage());
                            Toast.makeText(DetailPicture.this, "Failed to set wallpaper", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }


                })
                .submit();
        Toast.makeText(DetailPicture.this, "Wallpaper set successfully", Toast.LENGTH_SHORT).show();
    }

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options");
        builder.setItems(new CharSequence[]{"Add to album", "Set as Wallpaper", "Start referencing", "Detail"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý khi người dùng chọn một tùy chọn
                switch (which) {
                    case 0:
                        // Xử lý khi người dùng chọn "Add to album"
                        break;
                    case 1:
                        // Xử lý khi người dùng chọn "Set as Wallpaper"
                        setAsWallpaper(imagePaths.get(currentPosition));
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

    private void loadImage(int position) {
        Glide.with(this).load(Uri.parse(imagePaths.get(position))).into(imageView);
    }

    private void loadImageInfo() {
        imageInfoFragment.setImageInfo(getImageModel());
        // Add ImageInfo fragment to activity
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .add(R.id.imageInfo, imageInfoFragment)
                .commit();
    }

    private void loadQRCodeLink() {
        String uri = getImageModel().getRef();
        String qrCodeData = mainController.getImageController().recognizeQRCode(uri);
        if (qrCodeData != null) {
            qrLink.setText(qrCodeData);
            new Handler().postDelayed(() -> qrLink.setText(""), 5000);
        } else {
            Log.v("DetailPicture", "No QR Code data found");
        }
    }

    private void launchEditImageActivity() {
        Intent intent = new Intent(DetailPicture.this, EditImageActivity.class);
        long id = mainController.getImageController().getIdByRef(imagePaths.get(currentPosition));
        intent.putExtra("id", id);
        startActivityForResult(intent, REQUEST_CODE_EDIT_IMAGE);
    }

    private void deleteImage() {
        String uri = getImageModel().getRef();
        if (uri != null) {
            showDeleteConfirmationDialog(uri);
        } else {
            Toast.makeText(this, "No image to delete", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleImageInfo() {
        isImageInfoVisible = !isImageInfoVisible;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Log.v("DetailPicture", "toggleImageInfo: " + getSupportFragmentManager().findFragmentById(R.id.imageInfo).getView().getVisibility());
        if (isImageInfoVisible) {
            imageInfoView.setVisibility(View.VISIBLE);
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        } else {
            imageInfoView.setVisibility(View.GONE);
            transaction.setCustomAnimations(R.anim.slide_down, R.anim.slide_up);
        }
        transaction.commit();
    }

    private void showDeleteConfirmationDialog(String uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this image?");
        builder.setPositiveButton("Delete", (dialog, which) -> mainController.getImageController().deleteSelectedImage(uri));
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private ImageModel getImageModel() {
        long id = getIntent().getLongExtra("id", 0);
        return mainController.getImageController().getImageById(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EDIT_IMAGE && resultCode == RESULT_OK && data != null) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    @Override
    public void onNoticePassed(String data) {
        String where = "id = " + getIntent().getLongExtra("id", 0);
        mainController.getImageController().update("notice", data, where);
    }
}

