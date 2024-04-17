package com.example.albumgallery.view.activity;

import static com.example.albumgallery.utils.Constant.REQUEST_CODE_EDIT_IMAGE;
import static com.example.albumgallery.utils.Utilities.byteArrayToBitmap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class BeautyImageActivity extends AppCompatActivity {
    private MainController mainController;
    private ImageView mImageView;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mPaint;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private boolean isDrawingEnabled = false;
    private boolean isBrightnessActionClicked = false;
    private boolean isContrastActionClicked = false;
    Bitmap adjustedBitmap;
    SeekBar seekBarBrightness;
    SeekBar seekBarContrast;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_image);
        mainController = new MainController(this);
        mImageView = findViewById(R.id.imageView);

        appBarAction();

        String id = getIntent().getStringExtra("id");
        String imageURL = mainController.getImageController().getImageById(id).getRef();
        Glide.with(this)
                .asBitmap()
                .load(Uri.parse(imageURL))
                .error(R.drawable.placeholder_image) // Placeholder image in case of loading error
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e("Glide", "Image loading failed", e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        mBitmap = resource;
                        return false;
                    }
                })
                .into(mImageView);


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);

        // Set onTouchListener for drawing
        mImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isDrawingEnabled) { // Check if drawing is enabled
                    float x = event.getX();
                    float y = event.getY();

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchStart(x, y);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            touchMove(x, y);
                            break;
                        case MotionEvent.ACTION_UP:
                            touchUp();
                            break;
                    }
                    return true;
                } else {
                    return false; // Disable touch event when drawing is not enabled
                }
            }
        });

        seekBarBrightness = findViewById(R.id.seekBarBrightness);
        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Không cần thực hiện gì khi bắt đầu theo dõi
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Không cần thực hiện gì khi kết thúc theo dõi
            }
        });

        seekBarContrast = findViewById(R.id.seekBarContrast);
        seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateContrast(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Không cần thực hiện gì khi bắt đầu theo dõi
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Không cần thực hiện gì khi kết thúc theo dõi
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void appBarAction() {
        int[] buttonIds = {R.id.action_pen,R.id.action_brightness, R.id.action_contrast, R.id.action_filter, R.id.action_back};

        for (int buttonId : buttonIds) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(v -> {
                if (buttonId == buttonIds[0]) {
                    toggleDrawingMode();
                    isBrightnessActionClicked = false;
                    seekBarBrightness.setVisibility(View.GONE);
                    seekBarContrast.setVisibility(View.GONE);
                } else if (buttonId == buttonIds[1]) {
                    toggleBrightnessMode();
                    isDrawingEnabled = false;
                    seekBarContrast.setVisibility(View.GONE);
                } else if (buttonId == buttonIds[2]) {
                    toggleContrastMode();
                    isDrawingEnabled = false;
                    isBrightnessActionClicked = false;
                    seekBarBrightness.setVisibility(View.GONE);
                } else if (buttonId == buttonIds[3]) {
                    isDrawingEnabled = false;
                    isBrightnessActionClicked = false;
                    seekBarBrightness.setVisibility(View.GONE);
                    seekBarContrast.setVisibility(View.GONE);
                } else if (buttonId == buttonIds[4]) {
                    Intent intent = new Intent();
                    intent.putExtra("update", true);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }


    }
    private void toggleDrawingMode() {
        isDrawingEnabled = !isDrawingEnabled;
        if (isDrawingEnabled) {
            if (adjustedBitmap != null) {
                mImageView.setImageBitmap(adjustedBitmap);
            }
            Toast.makeText(this, "Drawing mode enabled", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Drawing mode disabled", Toast.LENGTH_SHORT).show();
        }

    }
    private void toggleBrightnessMode() {
        isBrightnessActionClicked = !isBrightnessActionClicked;
        if (isBrightnessActionClicked) {
            seekBarBrightness.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Brightness mode enabled", Toast.LENGTH_SHORT).show();
        } else {
            seekBarBrightness.setVisibility(View.GONE);
            Toast.makeText(this, "Brightness mode disabled", Toast.LENGTH_SHORT).show();
        }
    }
    private void toggleContrastMode() {
        isContrastActionClicked = !isContrastActionClicked;
        if (isContrastActionClicked) {
            seekBarContrast.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Brightness mode enabled", Toast.LENGTH_SHORT).show();
        } else {
            seekBarContrast.setVisibility(View.GONE);
            Toast.makeText(this, "Brightness mode disabled", Toast.LENGTH_SHORT).show();
        }
    }
    private void touchStart(float x, float y) {
        mCanvas = new Canvas(mBitmap);
        mX = x;
        mY = y;
    }
    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mCanvas.drawLine(mX, mY, x, y, mPaint);
            mX = x;
            mY = y;
            mImageView.setImageBitmap(mBitmap);
        }
    }
    private void touchUp() {
        mCanvas.drawLine(mX, mY, mX, mY, mPaint);
    }
    private void updateBrightness(int progress) {
        // Chuyển đổi giá trị progress từ khoảng [0, 100] thành mức độ độ sáng phù hợp
        float brightnessLevel = (progress - 50) * 2; // Điều chỉnh giá trị từ -100 đến 100

        if (adjustedBitmap == null) {
            adjustedBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap.getConfig());
        }
        // Tạo một Canvas mới với Bitmap được điều chỉnh
        Canvas canvas = new Canvas(adjustedBitmap);

        // Tạo một Paint để vẽ lên Canvas
        Paint paint = new Paint();

        // Thiết lập sự thay đổi độ sáng thông qua ColorMatrix
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                1, 0, 0, 0, brightnessLevel,
                0, 1, 0, 0, brightnessLevel,
                0, 0, 1, 0, brightnessLevel,
                0, 0, 0, 1, 0
        });
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);

        // Vẽ Bitmap gốc lên Canvas với Paint đã được điều chỉnh
        canvas.drawBitmap(mBitmap, 0, 0, paint);

        // Hiển thị Bitmap đã được điều chỉnh lên ImageView
        mImageView.setImageBitmap(adjustedBitmap);
    }
    private void updateContrast(int progress) {
        // Chuyển đổi giá trị progress từ khoảng [0, 100] thành mức độ độ tương phản phù hợp
        float contrastLevel = progress / 50f; // Điều chỉnh giá trị từ 0 đến 2

        if (adjustedBitmap == null) {
            adjustedBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap.getConfig());
        }
        // Tạo một Canvas mới với Bitmap được điều chỉnh
        Canvas canvas = new Canvas(adjustedBitmap);

        // Tạo một Paint để vẽ lên Canvas
        Paint paint = new Paint();

        // Thiết lập sự thay đổi độ tương phản thông qua ColorMatrix
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[] {
                contrastLevel, 0, 0, 0, 128 * (1 - contrastLevel),
                0, contrastLevel, 0, 0, 128 * (1 - contrastLevel),
                0, 0, contrastLevel, 0, 128 * (1 - contrastLevel),
                0, 0, 0, 1, 0
        });

        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorMatrixFilter);

        // Vẽ Bitmap gốc lên Canvas với Paint đã được điều chỉnh
        canvas.drawBitmap(mBitmap, 0, 0, paint);

        // Hiển thị Bitmap đã được điều chỉnh lên ImageView
        mImageView.setImageBitmap(adjustedBitmap);
    }

}