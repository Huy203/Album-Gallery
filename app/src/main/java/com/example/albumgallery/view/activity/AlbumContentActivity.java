package com.example.albumgallery.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.model.AlbumModel;
import com.example.albumgallery.model.ImageAlbumModel;
import com.example.albumgallery.view.adapter.AlbumInfoListener;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.example.albumgallery.view.fragment.AlbumInfo;
import com.example.albumgallery.view.listeners.ImageAdapterListener;
import com.google.android.material.button.MaterialButton;
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
        SlideShowActivity slideShowActivity = new SlideShowActivity();
        view = findViewById(R.id.albumInfo);

        album_id = mainController.getAlbumController().getAlbumIdByName(albumName);

        image_ids = mainController.getImageAlbumController().getImageIdsByAlbumId(album_id);

        updateCapacity();

        for (String image_id : image_ids) {
            String ref = mainController.getImageController().getImageRefById(image_id);
            imageURIs.add(ref);
        }

        imageAdapter = new ImageAdapter(this, imageURIs);
        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerViewAlbumContent);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();

        albumInfoFragment.setAlbumInfo(getAlbumModel());

        MaterialButton changeGridViewBtn = findViewById(R.id.changeGridViewBtn);
        changeGridViewBtn.setOnClickListener(this::changeViewAction);

        // Add ImageInfo fragment to activity
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up, R.anim.slide_down)
                .add(R.id.albumInfo, albumInfoFragment)
                .commit();
    }

    public AlbumModel getAlbumModel() {
        return mainController.getAlbumController().getAlbumById(String.valueOf(album_id));
    }

    private void handleInteractions() {
        Button backBtn = findViewById(R.id.backButtonAlbumContent);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainFragment = new Intent(AlbumContentActivity.this, MainFragmentController.class);
                mainFragment.putExtra("fragmentToLoad", "AlbumMain");
                startActivity(mainFragment);
                finish();
            }
        });
        // Set OnClickListener to the root view of the activity layout
        findViewById(android.R.id.content).setOnClickListener(v -> {
            if (isAlbumInfoVisible) {
                toggleAlbumInfo();
            }
        });
    }

    @Override
    public void handleImagePick(View itemView, String uri, int position) {
        Intent intent = new Intent(this, DetailPicture.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, itemView, "image");
        String id = mainController.getImageController().getIdByRef(uri);
        intent.putExtra("id", id);
        intent.putStringArrayListExtra("imagePathsAlbum", new ArrayList<>(imageURIs));
        intent.putExtra("position", position);
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
                List<ImageAlbumModel> image_albums = mainController.getImageAlbumController().getAllImageAlbums();
                for (ImageAlbumModel image_album : image_albums) {
                    if (Objects.equals(image_album.getAlbum_id(), id_album)) {
                        mainController.getImageAlbumController().deleteImageAlbum(image_album.getAlbum_id());
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
                        .setNegativeButton("No", (dialogInterface, i) -> {
                        })
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            mainController.getAlbumController().removeAlbumPasswordByName(albumName);
                            btnRemovePassword.setVisibility(View.GONE);
                            setPasswordField.setVisibility(View.VISIBLE);
                            Snackbar.make(view, "Password has been removed", Snackbar.LENGTH_SHORT).show();
                        });
                builder.show();
            }
        });

        TextInputEditText passwordInputAlbumInfo = (TextInputEditText) findViewById(R.id.passwordInputAlbumInfo);
        Button applyPassword = (Button) findViewById(R.id.applyPasswordAlbumInfo);
        passwordInputAlbumInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().length() > 0) {
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
        mainController.getAlbumController().update("notice", data, String.valueOf(album_id));
    }

    public void updateCapacity() {
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

    private void changeViewAction(View view) {
        PopupMenu popupMenu = new PopupMenu(AlbumContentActivity.this, view);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, getResources().getString(R.string.album_delete));
        popupMenu.getMenu().add(Menu.NONE, 1, 1, getResources().getString(R.string.album_info));
        popupMenu.getMenu().add(Menu.NONE, 2, 2, getResources().getString(R.string.slide_show));
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    // delete
                    showDeleteConfirmationDialog();
                    return true;
                case 1:
                    // info
                    toggleAlbumInfo();
                    return true;
                case 2:
                    // slide
                    startSlideShow();
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }
}
