package com.example.albumgallery.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.view.adapter.ImageAdapter;
import com.example.albumgallery.view.listeners.ImageAdapterListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class SelectImageActivity extends AppCompatActivity implements ImageAdapterListener {
    private RecyclerView recyclerMediaView;
    private List<String> imageURIs; //contains the list of image encoded.
    private ImageAdapter imageAdapter; //adapter for the recycler view
    private MainController mainController; //controller contains other controllers
    private TextView numberOfImagesSelected;
    List<String> selectedImageURLs;
    List<Task> selectedImageURLsTask;
    List<String> selectedImageURIs;
    String albumName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);

        mainController = new MainController(this);

        imageURIs = new ArrayList<>();
        selectedImageURLs = new ArrayList<>();
        selectedImageURLsTask = new ArrayList<>();

        selectedImageURIs = new ArrayList<>();
        numberOfImagesSelected = findViewById(R.id.numberOfSelectedImagesInSelectActivity);

        imageURIs.addAll(mainController.getImageController().getAllImageURLsUndeleted());

        imageAdapter = new ImageAdapter(this, imageURIs);
        recyclerMediaView = this.findViewById(R.id.recyclerViewForSelectImageActivity);
        recyclerMediaView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerMediaView.setAdapter(imageAdapter);

        imageAdapter.notifyDataSetChanged();
        imageAdapter.setMultipleChoiceEnabled(true);

        albumName = getIntent().getStringExtra("albumName");

//        imageAdapter.toggleMultipleChoiceImagesEnabled();


        handleInteractions();
    }

    private void handleInteractions() {
        Button btnCreateAlbum = (Button) findViewById(R.id.btnCreateAlbum);
        btnCreateAlbum.setOnClickListener(view -> {
            Intent intent = new Intent(SelectImageActivity.this, CreateAlbumActivity.class);
            // send the number of images to CreateAlbumActivity
            intent.putExtra("numOfImages", numberOfImagesSelected.getText().toString());
            // send the list of URLS to CreateAlbumActivity
            intent.putStringArrayListExtra("selectedImageURIs", (ArrayList<String>) selectedImageURIs);
            // set the afterSelectImage
            intent.putExtra("isSelected", true);

            // set the albumName
            intent.putExtra("albumName", albumName);

            startActivity(intent);
            finish();
        });

        ImageButton btnBack = (ImageButton) findViewById(R.id.backButtonSelectImageActivity);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectImageActivity.this, CreateAlbumActivity.class);
                intent.putExtra("albumName", albumName);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageAdapter.setMultipleChoiceEnabled(true);
    }

    @Override
    public void handleImagePick(View itemView, String uri, int position) {
        Intent intent = new Intent(this, DetailPicture.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, itemView, "image");
        String id = mainController.getImageController().getIdByRef(uri);
        intent.putExtra("id", id);
        intent.putExtra("position", position);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void getInteractedURIs(String uri) {
        if (!selectedImageURIs.contains(uri)) {
            selectedImageURIs.add(uri);
        } else {
            selectedImageURIs.remove(uri);
        }
    }

    @Override
    public void toggleMultipleChoice() {
        int length = imageAdapter.getSelectedItems().size(); // get the number of selected items
        numberOfImagesSelected.setText(length + " images selected");
    }
}
