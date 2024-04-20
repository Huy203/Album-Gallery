package com.example.albumgallery.view.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.AlbumController;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.model.AlbumModel;
import com.example.albumgallery.model.ImageAlbumModel;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.view.adapter.AlbumAdapter;
import com.example.albumgallery.view.adapter.AlbumInfoListener;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.example.albumgallery.view.fragment.AlbumInfo;
import com.example.albumgallery.view.fragment.ImageInfo;
import com.example.albumgallery.view.listeners.ImageAdapterListener;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlbumContentActivity extends AppCompatActivity implements ImageAdapterListener, AlbumInfoListener {
    private RecyclerView recyclerView;
    private List<String> imageURIs;
    private ImageAdapter imageAdapter;
    private MainController mainController;
    private String albumName;
    private TextView albumNameTxtView;
    private List<String> image_ids;
    private String album_id;
    private View view;
    private boolean isAlbumInfoVisible = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_content);

        handleInteractions();

        mainController = new MainController(this);
        image_ids = new ArrayList<>();
        imageURIs = new ArrayList<>();
        albumName = getIntent().getStringExtra("albumName");
        albumNameTxtView = (TextView) findViewById(R.id.albumNameAlbumContent);
        albumNameTxtView.setText(albumName);

        AlbumInfo albumInfoFragment = new AlbumInfo();
        Button AlbumInfo = findViewById(R.id.AlbumInfo);
        SlideShowActivity slideShowActivity = new SlideShowActivity();
        Button SlideShow = findViewById(R.id.SlideShow);
        view = findViewById(R.id.albumInfo);

        album_id = mainController.getAlbumController().getAlbumIdByName(albumName);
//        Log.d("album_id", Integer.toString(album_id));

        image_ids = mainController.getImageAlbumController().getImageIdsByAlbumId(album_id);

        updateCapacity();

        for(String image_id: image_ids) {
            String ref = mainController.getImageController().getImageRefById(image_id);
            Log.d("ref from id", ref);
            imageURIs.add(ref);
        }

        imageAdapter = new ImageAdapter(this, imageURIs);
        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerViewAlbumContent);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();

        AlbumInfo.setOnClickListener(v -> {
            toggleAlbumInfo();
        });

        SlideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSlideShow();
            }
        });

        albumInfoFragment.setAlbumInfo(getAlbumModel());

        // Add ImageInfo fragment to activity
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .add(R.id.albumInfo, albumInfoFragment)
                .commit();
    }

    public AlbumModel getAlbumModel() {
        Log.d("album content id", String.valueOf(album_id));
        return mainController.getAlbumController().getAlbumById(String.valueOf(album_id));
    }

    private void handleInteractions() {
        ImageButton backBtn = (ImageButton) findViewById(R.id.backButtonAlbumContent);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainFragment = new Intent(AlbumContentActivity.this, MainFragmentController.class);
                mainFragment.putExtra("fragmentToLoad", "AlbumMain");
                startActivity(mainFragment);
                finish();
            }
        });
        Button deleteBtn = (Button) this.findViewById(R.id.btnDelete);
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void getSelectedItemsCount( ){
//        Log.v("SelectedItems", count + " items selected");
//        numberOfImagesSelected.setText(count + " images selected");
//
//        for (int i = 0; i < count; i++) {
//            selectedImageURLsTask.add(Tasks.forResult(Uri.parse(imageURIs.get(i))));
//            Log.d("Deleted images task", selectedImageURLsTask.get(i).getResult().toString());
//        }
//
//        for (int i = 0; i < count; i++) {
//            selectedImageURLs.add(imageURIs.get(i));
//            Log.d("Deleted images", selectedImageURLs.get(i));
//        }
//
//        for (int i = 0; i < count; i++) {
//            selectedIds.add(ids.get(i));
//            Log.d("ids", ids.get(i));
//        }
    }

    @Override
    public void handleImagePick(View itemView, String uri, int position) {
        Intent intent = new Intent(this, DetailPicture.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, itemView, "image");
        String id = mainController.getImageController().getIdByRef(uri);
        intent.putExtra("id", id);
        intent.putExtra("position", position);
        Log.v("ImageAdapter", "Image selected: " + itemView);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void getInteractedURIs(String uri) {

    }

    @Override
    public void toggleMultipleChoice() {

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this album?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Call deleteSelectedImage() method from ImageController

                String id_album = mainController.getAlbumController().getAlbumIdByName(albumName);

                Log.d("check before name", albumName);
                Log.d("check before id", String.valueOf(id_album));
                List<ImageAlbumModel> image_albums = mainController.getImageAlbumController().getAllImageAlbums();
                Log.d("check delete image_album size", String.valueOf(image_albums.size()));
                for(ImageAlbumModel image_album : image_albums){
                    Log.d("check id", String.valueOf(image_album.getAlbum_id()));
                    if(Objects.equals(image_album.getAlbum_id(), id_album)){
                        mainController.getImageAlbumController().deleteImageAlbum(image_album.getAlbum_id());
                        Log.d("check delete album", String.valueOf(image_album.getAlbum_id()));
                    }
                }

                mainController.getAlbumController().deleteAlbum(albumName);

                Intent albumMainActivity = new Intent(AlbumContentActivity.this, MainFragmentController.class);
                albumMainActivity.putExtra("fragmentToLoad", "AlbumMain");
                startActivity(albumMainActivity);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    private void toggleAlbumInfo() {
        isAlbumInfoVisible = !isAlbumInfoVisible;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Log.v("AlbumContent", "toggleAlbumInfo: " + getSupportFragmentManager().findFragmentById(R.id.albumInfo).getView().getVisibility());
        Button btnRemovePassword = (Button) view.findViewById(R.id.btnRemovePassword);
        LinearLayout setPasswordField = (LinearLayout) view.findViewById(R.id.setPasswordField);
        String album_pw = mainController.getAlbumController().getPasswordByAlbumName(albumName);
        btnRemovePassword.setVisibility(album_pw.isEmpty() ? View.GONE : View.VISIBLE);
        setPasswordField.setVisibility(album_pw.isEmpty() ? View.VISIBLE : View.GONE);
        btnRemovePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(AlbumContentActivity.this);
                builder.setTitle("Are you sure to remove password ?")
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mainController.getAlbumController().removeAlbumPasswordByName(albumName);
                                btnRemovePassword.setVisibility(View.GONE);
                                setPasswordField.setVisibility(View.VISIBLE);
                                Snackbar.make(view, "Password has been removed", Snackbar.LENGTH_SHORT).show();
                            }
                        });
                builder.show();
            }
        });

        TextInputEditText passwordInputAlbumInfo = (TextInputEditText) findViewById(R.id.passwordInputAlbumInfo);
        Button applyPassword = (Button) findViewById(R.id.applyPasswordAlbumInfo);
        passwordInputAlbumInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().length() > 0) {
                    applyPassword.setEnabled(true);
                } else {
                    applyPassword.setEnabled(false);
                }
            }
        });
        applyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordInputAlbumInfo.getText().toString().trim();
                mainController.getAlbumController().update("password", password, "name = " + "'" + albumName + "'");
                setPasswordField.setVisibility(View.GONE);
                btnRemovePassword.setVisibility(View.VISIBLE);
            }
        });


        if (isAlbumInfoVisible) {
            view.setVisibility(View.VISIBLE);
            transaction.setCustomAnimations(R.anim.slide_up, R.anim.slide_down);
        } else {
            view.setVisibility(View.GONE);
            transaction.setCustomAnimations(R.anim.slide_down, R.anim.slide_up);
        }
        transaction.commit();
    }

    @Override
    public void onNoticePassed(String data) {
//        String where = "id = " + getIntent().getLongExtra("id", 0);
        Log.d("test update where clause", String.valueOf(album_id));
        mainController.getAlbumController().update("notice", data, String.valueOf(album_id));
    }

    public void updateCapacity(){
        long capacity = mainController.getImageController().getTotalCapacityFromImageIDs(image_ids);
        mainController.getAlbumController().update("capacity", String.valueOf(capacity), "id = " + "'" + String.valueOf(album_id) + "'");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    private void startSlideShow() {
        Intent intent = new Intent(this, SlideShowActivity.class);
        intent.putStringArrayListExtra("imageURIs", new ArrayList<>(imageURIs));
        startActivity(intent);
    }
}
