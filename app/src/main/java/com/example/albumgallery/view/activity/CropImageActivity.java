package com.example.albumgallery.view.activity;

import static com.example.albumgallery.utils.Utilities.bitmapToByteArray;
import static com.example.albumgallery.utils.Utilities.convertFromUriToBitmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    Bitmap croppedBitmap;

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

            // Calculate square size based on minimum of width and height
            int squareSize = Math.min(width, height);
            int left = (width - squareSize) / 2;
            int top = (height - squareSize) / 2;
            int right = left + squareSize;
            int bottom = top + squareSize;

            // Set initial position and size of cropRect
            cropRect = new Rect(left, top, right, bottom);

            // Set initial position and size of overlayView
            overlayView.getLayoutParams().width = squareSize;
            overlayView.getLayoutParams().height = squareSize;
            overlayView.setX(left);
            overlayView.setY(top);
            overlayView.requestLayout();
        });

        // Touch listener for overlayView to adjust cropRect size and position
        overlayView.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private float startY;
            private int startWidth;
            private int startHeight;
            private boolean isCornerDragged = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        startWidth = overlayView.getWidth();
                        startHeight = overlayView.getHeight();

                        // Kiểm tra nếu điểm chạm đủ gần cạnh (ví dụ: trong bán kính 50px)
                        float edgeRadius = 50; // Điều chỉnh kích thước bán kính của cạnh tại đây
                        float distanceFromLeftEdge = Math.abs(startX - overlayView.getX());
                        float distanceFromTopEdge = Math.abs(startY - overlayView.getY());
                        float distanceFromRightEdge = Math.abs(startX - (overlayView.getX() + overlayView.getWidth()));
                        float distanceFromBottomEdge = Math.abs(startY - (overlayView.getY() + overlayView.getHeight()));

                        if (distanceFromLeftEdge <= edgeRadius || distanceFromTopEdge <= edgeRadius || distanceFromRightEdge <= edgeRadius || distanceFromBottomEdge <= edgeRadius) {
                            isCornerDragged = true; // Nếu điểm chạm đủ gần cạnh, cho phép di chuyển cạnh
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isCornerDragged) {
                            float dx = event.getRawX() - startX;
                            float dy = event.getRawY() - startY;

                            int newWidth = (int) (startWidth + dx);
                            int newHeight = (int) (startHeight + dy);

                            // Cập nhật kích thước overlayView
                            updateOverlaySize(newWidth, newHeight);
                        } else {
                            // Nếu không phải di chuyển cạnh, thực hiện di chuyển bên trong
                            float x = event.getRawX() - startX;
                            float y = event.getRawY() - startY;

                            // Cập nhật vị trí của overlayView
                            overlayView.setX(overlayView.getX() + x);
                            overlayView.setY(overlayView.getY() + y);

                            // Lưu vị trí mới của điểm chạm
                            startX = event.getRawX();
                            startY = event.getRawY();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isCornerDragged = false; // Kết thúc sự kiện khi người dùng nhả tay
                        break;
                }
                return true;
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
        // Lấy bitmap gốc từ cropImageView
        Bitmap originalBitmap = ((BitmapDrawable) cropImageView.getDrawable()).getBitmap();

        // Tính toán phần bao phủ của overlayView trên ảnh
        int overlayWidth = overlayView.getWidth();
        int overlayHeight = overlayView.getHeight();
        int overlayX = (int) overlayView.getX();
        int overlayY = (int) overlayView.getY();

        // Đảm bảo không cắt ra khỏi biên ảnh
        overlayX = Math.max(0, overlayX);
        overlayY = Math.max(0, overlayY);
        overlayWidth = Math.min(overlayWidth, originalBitmap.getWidth() - overlayX);
        overlayHeight = Math.min(overlayHeight, originalBitmap.getHeight() - overlayY);

        // Tạo một Bitmap mới để lưu phần bên trong overlay
        croppedBitmap = Bitmap.createBitmap(overlayWidth, overlayHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(croppedBitmap);
        Rect srcRect = new Rect(overlayX, overlayY, overlayX + overlayWidth, overlayY + overlayHeight);
        Rect destRect = new Rect(0, 0, overlayWidth, overlayHeight);
        canvas.drawBitmap(originalBitmap, srcRect, destRect, null);

        // Hiển thị ảnh đã cắt trong cropImageView
        cropImageView.setImageBitmap(croppedBitmap);
    }


    public void saveAction(View view) {
        if (croppedBitmap != null) {
            // Perform actions with the cropped bitmap
            Intent intent = new Intent(this, EditImageActivity.class);
            intent.putExtra("imageByteArray", bitmapToByteArray(croppedBitmap));
            setResult(RESULT_OK, intent);
            finish();
        } else {
            // Handle the case where no cropping has been performed
            Toast.makeText(this, "Please crop the image before saving.", Toast.LENGTH_SHORT).show();
        }
    }

    public void backAction(View view) {
        finish();
    }

}