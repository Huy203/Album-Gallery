package com.example.albumgallery.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.Tasks;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_image);

        mainController = new MainController(this);

        imageURIs = new ArrayList<>();
        selectedImageURLs = new ArrayList<>();
        selectedImageURLsTask = new ArrayList<>();

        selectedImageURIs = new ArrayList<>();
        numberOfImagesSelected = (TextView) findViewById(R.id.numberOfSelectedImagesInSelectActivity);

        imageURIs.addAll(mainController.getImageController().getAllImageURLsSortByDate());
        imageAdapter = new ImageAdapter(this, imageURIs);
        recyclerMediaView = this.findViewById(R.id.recyclerViewForSelectImageActivity);
        recyclerMediaView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerMediaView.setAdapter(imageAdapter);

        imageAdapter.notifyDataSetChanged();
        imageAdapter.toggleMultipleChoiceImagesEnabled();

        handleInteractions();
    }

    private void handleInteractions() {
        Button btnCreateAlbum = (Button) findViewById(R.id.btnCreateAlbum);
        btnCreateAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectImageActivity.this, CreateAlbumActivity.class);
                // send the number of images to CreateAlbumActivity
                intent.putExtra("numOfImages", numberOfImagesSelected.getText().toString());
                // send the list of URLS to CreateAlbumActivity
                intent.putStringArrayListExtra("selectedImageURIs", (ArrayList<String>) selectedImageURIs);
                // set the afterSelectImage
                intent.putExtra("isSelected", true);

                startActivity(intent);
                finish();
            }
        });

        ImageButton btnBack = (ImageButton) findViewById(R.id.backButtonSelectImageActivity);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectImageActivity.this, CreateAlbumActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void getSelectedItemsCount(int count) {
        Log.v("SelectedItems", count + " items selected");
        numberOfImagesSelected.setText(count + " images selected");

        for (int i = 0; i < count; i++) {
            selectedImageURLsTask.add(Tasks.forResult(Uri.parse(imageURIs.get(i))));
            Log.d("Deleted images task", selectedImageURLsTask.get(i).getResult().toString());
        }

        for (int i = 0; i < count; i++) {
            selectedImageURLs.add(imageURIs.get(i));
            Log.d("Deleted images", selectedImageURLs.get(i));
        }
    }

    @Override
    public void handleImagePick(View itemView, String uri, int position) {
        Intent intent = new Intent(this, DetailPicture.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, itemView, "image");
        long id = mainController.getImageController().getIdByRef(uri);
        intent.putExtra("id", id);
        intent.putExtra("position", position);
        Log.v("ImageAdapter", "Image selected: " + itemView);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void getInteractedURIs(String uri) {
        if(!selectedImageURIs.contains(uri)) {
            selectedImageURIs.add(uri);
        }
//        for(String u: selectedImageURIs) {
//            Log.d("current uris", u);
//        }
    }
}
