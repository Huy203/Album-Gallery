package com.example.albumgallery.view.activity;

import static android.app.ProgressDialog.show;
import static com.example.albumgallery.utils.Constant.REQUEST_CODE_EDIT_IMAGE;
import static com.example.albumgallery.utils.Utilities.convertFromBitmapToUri;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.example.albumgallery.helper.SharePreferenceHelper;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.view.fragment.ImageInfo;
import com.example.albumgallery.view.listeners.ImageInfoListener;
import com.example.albumgallery.view.listeners.OnSwipeTouchListener;
import com.example.albumgallery.view.listeners.TextRecognitionListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DetailPicture extends AppCompatActivity implements ImageInfoListener, TextRecognitionListener {

    protected MainController mainController;
    protected ImageView imageView;
    protected List<String> imagePaths;
    protected TextView qrLink;
    protected int currentPosition;
    protected View imageInfoView;
    protected ImageInfo imageInfoFragment;
    protected boolean isImageInfoVisible = false;
    protected boolean isDeleted;
    protected AlertDialog optionsDialog;
    ImageModel imageModel;

    List<String> textRecognized;
    List<Rect> boundingBoxes;

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

    @Override
    protected void onResume() {
        super.onResume();
        update();

        setIconTintButton(findViewById(R.id.action_like), imageModel.getIs_favourited());
    }

    private void initializeViews() {
        mainController = new MainController(this);
        imagePaths = mainController.getImageController().getAllImageURLsUndeleted();
        currentPosition = getIntent().getIntExtra("position", 0);

        imageView = findViewById(R.id.memeImageView);
        qrLink = findViewById(R.id.qrLink);

        imageInfoView = findViewById(R.id.imageInfo);
        imageInfoFragment = new ImageInfo();

        textRecognized = new ArrayList<>();
        boundingBoxes = new ArrayList<>();

        update();
    }

    @SuppressLint("NonConstantResourceId")
    private void setupListeners() {
        // Swipe gestures
        imageView.setOnTouchListener(new OnSwipeTouchListener(this, imageView) {
            @Override
            public void onSwipeLeft() {
                if (currentPosition < imagePaths.size() - 1) {
                    currentPosition++;
                    loadImage(currentPosition);
                } else {
                    // Notify the user that this is the last image
                    Toast.makeText(DetailPicture.this, "This is the last image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSwipeRight() {
                if (currentPosition > 0) {
                    currentPosition--;
                    loadImage(currentPosition);
                } else {
                    // Notify the user that this is the first image
                    Toast.makeText(DetailPicture.this, "This is the first image", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setIconTintButton(MaterialButton button, boolean temp) {
        button.setIconTint(ColorStateList.valueOf(getResources().getColor(temp ? R.color.blue_500 :
                SharePreferenceHelper.isDarkModeEnabled(this) ? R.color.white : // white
                        R.color.black)));
    }

    public void setAsWallpaper(String imagePath) {
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
        builder.setItems(new CharSequence[]{"Add to album", "Set as Wallpaper"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Xử lý khi người dùng chọn một tùy chọn
                switch (which) {
                    case 0:
                        // Xử lý khi người dùng chọn "Add to album"
                        addToAlbumBtnHandler();
                        break;
                    case 1:
                        // Xử lý khi người dùng chọn "Set as Wallpaper"
                        setAsWallpaper(imagePaths.get(currentPosition));
                        break;
                }
            }
        });

    }

    protected void loadImage(int position) {
        Glide.with(this).load(Uri.parse(imagePaths.get(position))).into(imageView);
    }

    private void loadImageInfo() {
        TextView nameImage = findViewById(R.id.nameTextView);
        TextView dateImage = findViewById(R.id.timeTextView);
        nameImage.setText(imageModel.getName());
        dateImage.setText(imageModel.getCreated_at());
        imageInfoFragment.setImage(imageModel);
        // Add ImageInfo fragment to activity
        getSupportFragmentManager().beginTransaction()
                .add(R.id.imageInfo, imageInfoFragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadQRCodeLink() {
        String uri = imageModel.getRef();
        String qrCodeData = mainController.getImageController().recognizeQRCode(uri);
        if (qrCodeData != null) {
            qrLink.setText(qrCodeData);
            new Handler().postDelayed(() -> qrLink.setText(""), 3000);
        } else {
            Log.v("DetailPicture", "No QR Code data found");
        }
    }

    private void loadRecognizeText() {
        String uri = imageModel.getRef();
        Log.v("DetailPicture", "uriImage: " + uri);
        mainController.getImageController().recognizeText(uri);
    }

    private void launchEditImageActivity() {
    }

    private void deleteImage() {
    }

    protected void toggleImageInfo() {
        isImageInfoVisible = !isImageInfoVisible;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isImageInfoVisible) {
            imageInfoView.setVisibility(View.VISIBLE);
        } else {
            imageInfoView.setVisibility(View.GONE);
        }
        transaction.commit();
    }

    private void shareImageAndText(Bitmap bitmap) {
        Uri uri = convertFromBitmapToUri(this, bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);

        // putting uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        // adding text to share
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image");

        // Add subject Here
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");

        // setting type to image
        intent.setType("image/png");

        // calling startactivity() to share
        startActivity(Intent.createChooser(intent, "Share Via"));
    }

    protected void showDeleteConfirmationDialog(String uri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this image?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Log.v("DetailPicture", "Image deleted successfully");
                isDeleted = mainController.getImageController().isDeleteImage(imageModel.getId());
                toggleDeleteImage(imageModel.getId());
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addToAlbumBtnHandler() {
        List<String> albumNames = mainController.getAlbumController().getAlbumNames();
        LayoutInflater inflater = DetailPicture.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.albums_dialog, null);
        RadioGroup albumGroup = dialogView.findViewById(R.id.albumDialog);

        for (String a : albumNames) {
            RadioButton albumBtn = new RadioButton(DetailPicture.this);
            albumBtn.setText(a);
            albumGroup.addView(albumBtn);
        }

        MaterialAlertDialogBuilder albumsDialog = new MaterialAlertDialogBuilder(DetailPicture.this);
        albumsDialog.setView(dialogView)
                .setTitle("Choose an album")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int selectedRadioButtonId = albumGroup.getCheckedRadioButtonId();
                        RadioButton selectedRadioBtn = dialogView.findViewById(selectedRadioButtonId);
                        if (selectedRadioBtn != null) {
                            String selectedAlbum = selectedRadioBtn.getText().toString();
                            String album_id = mainController.getAlbumController().getAlbumIdByName(selectedAlbum);
                            String image_id = mainController.getImageController().getIdByRef(imagePaths.get(currentPosition));
//                            Log.d("add to album", Long.toString(album_id) + " " + image_id);
                            mainController.getImageAlbumController().addImageAlbum(image_id, album_id);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        albumsDialog.show();
    }

    private void update() {
        String id = getIntent().getStringExtra("id");
        Log.v("DetailPicture", "id: " + id);
        imageModel = mainController.getImageController().getImageById(id);
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
        String where = "id = '" + getIntent().getStringExtra("id") + "'";
        mainController.getImageController().update("notice", data, where);
    }
    @Override
    public void onTimePassed(String data) {
        String where = "id = '" + getIntent().getStringExtra("id") + "'";
        mainController.getImageController().update("created_at", data, where);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (optionsDialog != null) {
            optionsDialog.dismiss();
            optionsDialog = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainController = null;
        imageView = null;
        imagePaths = null;
        qrLink = null;
        imageInfoView = null;
        imageInfoFragment = null;
        optionsDialog = null;
    }

    public void backAction(View view) {
        Intent intent = new Intent();
        intent.putExtra("update", false);
        setResult(RESULT_OK, intent);
        supportFinishAfterTransition();
    }

    public void editAction(View view) {
        Intent intent = new Intent(DetailPicture.this, EditImageActivity.class);
        String id = getIntent().getStringExtra("id");
        intent.putExtra("id", id);
        startActivityForResult(intent, REQUEST_CODE_EDIT_IMAGE);
    }

    public void menuAction(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.custom_options_dialog, null);
        builder.setView(dialogView);
        AlertDialog optionsDialog = builder.create();

        // Khởi tạo các button trong menu dialog
        Button buttonAddToAlbum = dialogView.findViewById(R.id.buttonAddToAlbum);
        Button buttonSetAsWallpaper = dialogView.findViewById(R.id.buttonSetAsWallpaper);

        // Xử lý sự kiện khi click vào từng button
        buttonAddToAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToAlbumBtnHandler();
                optionsDialog.dismiss();
            }
        });

        buttonSetAsWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAsWallpaper(imagePaths.get(currentPosition));
                optionsDialog.dismiss();
            }
        });

        optionsDialog.show();
    }

    public void shareAction(View view) {
        Glide.with(this)
                .asBitmap()
                .load(Uri.parse(imagePaths.get(currentPosition)))
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.e("DetailPicture", "Failed to load image: " + e.getMessage());
                        Toast.makeText(DetailPicture.this, "Failed to share image", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        shareImageAndText(resource);
                        return false;
                    }
                })
                .submit();
    }

    public void likeAction(View view) {
        mainController.getImageController().toggleFavoriteImage((imageModel.getId()));
        update();
        setIconTintButton((MaterialButton) view, imageModel.getIs_favourited());
    }

    public void infoAction(View view) {
        isImageInfoVisible = !isImageInfoVisible;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isImageInfoVisible) {
            imageInfoView.setVisibility(View.VISIBLE);
        } else {
            imageInfoView.setVisibility(View.GONE);
        }
        transaction.commit();

        setIconTintButton((MaterialButton) view, isImageInfoVisible);
    }

    public void deleteAction(View view) {
        String uri = imageModel.getRef();
        if (uri != null) {
            showDeleteConfirmationDialog(uri);
        } else {
            Toast.makeText(this, "No image to delete", Toast.LENGTH_SHORT).show();
        }
    }

    public void ocrAction(View view){
        loadRecognizeText();
    }

    private void toggleDeleteImage(String id) {
        Log.d("update delete successfully 2", "ok");
        isDeleted = !isDeleted;
        mainController.getImageController().setDelete(id, isDeleted);
    }

    protected ImageModel getImageModel() {
        String id = getIntent().getStringExtra("id");
        Log.d("image content id", String.valueOf(id));
        return mainController.getImageController().getImageById(id);
    }
    @Override
    public void onTextRecognized(List<String> textRecognized, List<Rect> boundingBoxes, Bitmap bitmap) {
        // You can also draw bounding boxes on an ImageView
        Bitmap bitmapWithBoxes = drawBoundingBoxesOnBitmap(bitmap, boundingBoxes);
        imageView.setImageBitmap(bitmapWithBoxes);

        // Store the recognized text and bounding boxes for later use
        this.textRecognized = textRecognized;
        this.boundingBoxes = boundingBoxes;
    }

    private Bitmap drawBoundingBoxesOnBitmap(Bitmap originalBitmap, List<Rect> boundingBoxes) {
        Bitmap bitmapWithBoxes = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmapWithBoxes);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);

        RelativeLayout parentLayout = findViewById(R.id.imageContainer);

        for (int i = 0; i < boundingBoxes.size(); i++) {
            Rect rect = boundingBoxes.get(i);
            canvas.drawRect(rect, paint);

            TextView boxView = new TextView(this);
            boxView.setBackgroundColor(Color.TRANSPARENT); // Set the background color to transparent

            // Calculate the position and size of the box relative to the parent layout
            float xScaleFactor = (float) parentLayout.getWidth() / originalBitmap.getWidth();
            float yScaleFactor = (float) parentLayout.getHeight() / originalBitmap.getHeight();

            int left = (int) (rect.left * xScaleFactor);
            int top = (int) (rect.top * yScaleFactor);
            int width = (int) (rect.width() * xScaleFactor);
            int height = (int) (rect.height() * yScaleFactor);


            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
            layoutParams.leftMargin = left;
            layoutParams.topMargin = top;
            boxView.setLayoutParams(layoutParams);

            float textSize = height * 0.9f; // Adjust this value as needed
            boxView.setTextSize(textSize);

            int finalI = i;
            boxView.setTextIsSelectable(true);
            boxView.setTextColor(getResources().getColor(R.color.none));

            boxView.setOnClickListener(v -> {
                for (int k = 0; k < boundingBoxes.size(); k++) {
                    parentLayout.getChildAt(k).setBackgroundColor(Color.TRANSPARENT);
                }
                // Handle the click event
                String recognizedText = textRecognized.get(finalI);
                boxView.setText(recognizedText);
            });

            // Add the View to the parent layout
            parentLayout.addView(boxView);
        }
        return bitmapWithBoxes;
    }


}