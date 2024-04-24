package com.example.albumgallery.view.activity;

import static com.example.albumgallery.utils.Utilities.bitmapToByteArray;
import static com.example.albumgallery.utils.Utilities.convertFromUriToBitmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;

public class CropImageActivity extends AppCompatActivity {
    private MainController mainController;
    private ImageView cropImageView;
    private Spinner aspectRatioSpinner;
    private boolean hasChanges = false;
    private View overlayView;
    private Rect cropRect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        cropImageView = findViewById(R.id.cropImageView);
        aspectRatioSpinner = findViewById(R.id.aspectRatioSpinner);
        overlayView = findViewById(R.id.overlayView);

        // Ensure the dimensions of cropImageView are set properly
        cropImageView.post(() -> {
            int width = cropImageView.getWidth();
            int height = cropImageView.getHeight();

            // Tính toán kích thước ô vuông dựa trên kích thước mặc định của cropImageView
            int squareSize = Math.min(width, height);
            int left = (width - squareSize) / 2;
            int top = (height - squareSize) / 2;
            int right = left + squareSize;
            int bottom = top + squareSize;

            // Thiết lập kích thước và vị trí của overlayView để hiển thị ô vuông
            overlayView.getLayoutParams().width = squareSize;
            overlayView.getLayoutParams().height = squareSize;
            overlayView.setX(left);
            overlayView.setY(top);
            overlayView.requestLayout();

            // Initialize cropRect with initial position and size
            cropRect = new Rect(left, top, right, bottom);
        });


        // Thiết lập sự kiện kéo thả cho overlay view để điều chỉnh kích thước và vị trí của khung cắt
        overlayView.setOnTouchListener(new View.OnTouchListener() {
            private float startX, startY;
            private boolean isDragging = false;
            private boolean isResizing = false;
            private int resizeAreaSize = 50; // Define the size of the resize area

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        isDragging = cropRect.contains((int) startX, (int) startY);
                        isResizing = isResizeArea((int) startX, (int) startY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isDragging) {
                            float dx = event.getX() - startX;
                            float dy = event.getY() - startY;
                            cropRect.left += dx;
                            cropRect.top += dy;
                            cropRect.right += dx;
                            cropRect.bottom += dy;
                            startX = event.getX();
                            startY = event.getY();
                            updateCrop();
                        } else if (isResizing) {
                            float dx = event.getX() - startX;
                            float dy = event.getY() - startY;
                            if (cropRect.width() + dx > 0 && cropRect.height() + dy > 0) {
                                cropRect.right += dx;
                                cropRect.bottom += dy;
                                startX = event.getX();
                                startY = event.getY();
                                updateCrop();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isDragging = false;
                        isResizing = false;
                        break;
                }
                return true;
            }

            // Helper method to check if the touch point is in the resize area
            private boolean isResizeArea(int x, int y) {
                return (x > cropRect.right - resizeAreaSize && x < cropRect.right + resizeAreaSize &&
                        y > cropRect.bottom - resizeAreaSize && y < cropRect.bottom + resizeAreaSize);
            }
        });

        mainController = new MainController(this);

        String id = getIntent().getStringExtra("id");
        String imageURL = mainController.getImageController().getImageById(id).getRef();
        Log.d("CropImageActivity", "Image URL: " + imageURL); // Check if imageURL is correct
        Glide.with(this)
                .asBitmap()
                .load(Uri.parse(imageURL))
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e("CropImageActivity", "Failed to load image: " + e.getMessage()); // Log error
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        cropImageView.setImageBitmap(resource);
                        setCropAspectRatio("1:1");
                        updateCrop();
                        return false;
                    }
                })
                .submit();

        setupAspectRatioSpinner();
    }


//    private void showAcceptChangesDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Accept Changes");
//        builder.setMessage("Do you want to accept changes?");
//        builder.setPositiveButton("Yes", (dialog, which) -> goBack(true));
//        builder.setNegativeButton("No", (dialog, which) -> goBack(false));
//        if (hasChanges) {
//            builder.show();
//        } else {
//            goBack(false);
//        }
//    }

    private void setupAspectRatioSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.aspect_ratios, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        aspectRatioSpinner.setAdapter(adapter);

        aspectRatioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRatio = (String) parent.getItemAtPosition(position);
                Log.v("CropImageActivity", "Selected ratio: " + selectedRatio);
                setCropAspectRatio(selectedRatio);
                updateCrop();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    private void setCropAspectRatio(String ratio) {
        int width = cropImageView.getWidth();
        int height = cropImageView.getHeight();

        switch (ratio) {
            case "1:1":
                // Calculate square size based on minimum of width and height
                int squareSize = Math.min(width, height);
                int left = (width - squareSize) / 2;
                int top = (height - squareSize) / 2;
                int right = left + squareSize;
                int bottom = top + squareSize;
                cropRect.set(left, top, right, bottom);
                updateOverlaySize(squareSize, squareSize);
                break;
            case "4:3":
                // Calculate width and height based on 4:3 aspect ratio
                int rectWidth = width;
                int rectHeight = (int) (width * 3.0 / 4.0);
                int rectTop = (height - rectHeight) / 2;
                cropRect.set(0, rectTop, rectWidth, rectTop + rectHeight);
                updateOverlaySize(width, rectHeight);
                break;
            case "Original":
                // Reset cropRect to cover the entire image
                cropRect.set(0, 0, width, height);
                updateOverlaySize(width, height);
                break;
            case "3:2":
                // Calculate width and height based on 3:2 aspect ratio
                int rectWidth32 = width;
                int rectHeight32 = (int) (width * 2.0 / 3.0);
                int rectTop32 = (height - rectHeight32) / 2;
                cropRect.set(0, rectTop32, rectWidth32, rectTop32 + rectHeight32);
                updateOverlaySize(width, rectHeight32);
                break;
            case "16:9":
                // Calculate width and height based on 16:9 aspect ratio
                int rectWidth169 = width;
                int rectHeight169 = (int) (width * 9.0 / 16.0);
                int rectTop169 = (height - rectHeight169) / 2;
                cropRect.set(0, rectTop169, rectWidth169, rectTop169 + rectHeight169);
                updateOverlaySize(width, rectHeight169);
                break;
        }
        overlayView.setX(cropRect.left);
        overlayView.setY(cropRect.top);

        updateCrop();
    }

    private void updateCrop() {
        // Cập nhật vị trí của overlay view để hiển thị khung cắt mới
        overlayView.layout(cropRect.left, cropRect.top, cropRect.right, cropRect.bottom);
    }

    private void updateOverlaySize(int width, int height) {
        overlayView.getLayoutParams().width = width;
        overlayView.getLayoutParams().height = height;
        overlayView.requestLayout();
    }


    public void cropAction(View view) {
        hasChanges = true;
        Bitmap originalBitmap = ((BitmapDrawable) cropImageView.getDrawable()).getBitmap();

        int left = Math.max(cropRect.left, 0);
        int top = Math.max(cropRect.top, 0);
        int right = Math.min(cropRect.right, originalBitmap.getWidth());
        int bottom = Math.min(cropRect.bottom, originalBitmap.getHeight());

        // Tạo một bitmap mới từ phạm vi của originalBitmap trong cropRect
        Bitmap croppedBitmap = Bitmap.createBitmap(originalBitmap, left, top, right - left, bottom - top);

        // Hiển thị ảnh đã cắt trên giao diện
        cropImageView.setImageBitmap(croppedBitmap);
    }



    public void saveAction(View view) {
        //Bitmap croppedImage = cropImageView.getCroppedImage();

        Intent intent = new Intent(this, EditImageActivity.class);
        //intent.putExtra("imageByteArray", bitmapToByteArray(croppedImage));
        setResult(RESULT_OK, intent);
        finish();
    }
    public void backAction(View view) {
        finish();
    }

}
