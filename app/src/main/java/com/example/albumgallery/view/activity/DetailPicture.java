package com.example.albumgallery.view.activity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.controller.OnSwipeTouchListener;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.view.adapter.ImageInfoListener;
import com.example.albumgallery.view.fragment.ImageInfo;

import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.AnimRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;
import java.util.List;

public class DetailPicture extends AppCompatActivity implements ImageInfoListener {
    private MainController mainController;
    private ImageView imageView;
    private ImageView heartButton;
    private List<String> imagePaths;
    private int currentPosition;
    private View view;
    private boolean isImageInfoVisible = false;
    private boolean isFavorite;

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
        ImageInfo imageInfoFragment = new ImageInfo();

        ImageView backButton = findViewById(R.id.backButton);
        ImageView pencilButton = findViewById(R.id.pencilButton);
        heartButton = (ImageView) findViewById(R.id.heartButton);
        ImageView ellipsisButton = findViewById(R.id.ellipsisButton);
        ImageView trashButton = findViewById(R.id.trashButton);
        Button ImageInfo = findViewById(R.id.ImageInfo);
        view = findViewById(R.id.imageInfo);

        pencilButton.setOnClickListener(v -> {
            Intent intent = new Intent(DetailPicture.this, EditImageActivity.class);

            long id = mainController.getImageController().getIdByRef(imagePaths.get(currentPosition));
            intent.putExtra("id", id);
            startActivity(intent);
            finish();
        });
        backButton.setOnClickListener(v -> {
            //Intent intent = new Intent(DetailPicture.this, HomeScreen.class);
//            startActivity(intent);
            supportFinishAfterTransition();
        });

        ellipsisButton.setOnClickListener(v -> {
            // Hiển thị menu hoặc dialog chọn tùy chọn
            showOptionsDialog();
        });
        String uri = getImageModel().getRef();
        trashButton.setOnClickListener(v -> {
            if (uri != null) {
                // Call deleteSelectedImage() method from ImageController
                // mainController.getImageController().deleteSelectedImage(uri, 0);
                showDeleteConfirmationDialog(uri);
            } else {
                Toast.makeText(this, "No image to delete", Toast.LENGTH_SHORT).show();
            }
        });

        // set image to favorite
//        long id = mainController.getImageController().getIdByRef(imagePaths.get(currentPosition));
        long id = mainController.getImageController().getIdByRef(uri);
        isFavorite = mainController.getImageController().isFavoriteImage(id);
        Log.d("test favorite", Long.toString(id));
        Log.d("test favorite", Boolean.toString(isFavorite));
        setFavoriteIcon(isFavorite);
        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainController.getImageController().toggleFavoriteImage(id);
                isFavorite = mainController.getImageController().isFavoriteImage(id);
                setFavoriteIcon(isFavorite);
            }
        });

        //        // Lấy ảnh từ image adapter, hiển thị vào edit image screen.
//        String imagePath = getIntent().getStringExtra("imagePath");
//        ImageView imageView = findViewById(R.id.memeImageView);
//        Glide.with(this).load(Uri.parse(imagePath)).into(imageView);
        ImageInfo.setOnClickListener(v -> {
            toggleImageInfo();
        });



        imageInfoFragment.setImageInfo(getImageModel());

        // Add ImageInfo fragment to activity
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .add(R.id.imageInfo, imageInfoFragment)
                .commit();


        ImageView imageView = findViewById(R.id.memeImageView);
        Glide.with(this).load(Uri.parse(uri)).into(imageView);
    }

    private void showDeleteConfirmationDialog(String uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this image?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call deleteSelectedImage() method from ImageController
                mainController.getImageController().deleteSelectedImage(uri);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void loadImage(int position) {
        Glide.with(this).load(Uri.parse(imagePaths.get(position))).into(imageView);
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

    private void toggleImageInfo() {
        isImageInfoVisible = !isImageInfoVisible;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Log.v("DetailPicture", "toggleImageInfo: " + getSupportFragmentManager().findFragmentById(R.id.imageInfo).getView().getVisibility());
        if (isImageInfoVisible) {
            view.setVisibility(View.VISIBLE);
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        } else {
            view.setVisibility(View.GONE);
            transaction.setCustomAnimations(R.anim.slide_down, R.anim.slide_up);
        }
        transaction.commit();
    }

    public ImageModel getImageModel() {
        long id = getIntent().getLongExtra("id", 0);
        return mainController.getImageController().getImageById(id);
    }

    @Override
    public void onNoticePassed(String data) {
        String where = "id = " + getIntent().getLongExtra("id", 0);
        mainController.getImageController().update("notice", data, where);
    }

    public MainController getMainController() {
        return mainController;
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

    public void setFavoriteIcon(boolean isFavorite) {
        if(isFavorite) {
            heartButton.setImageResource(R.drawable.heart_red);
        } else {
            heartButton.setImageResource(R.drawable.heart);
        }
    }

}

