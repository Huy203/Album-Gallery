package com.example.albumgallery.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.albumgallery.view.adapter.ImageAdapterListener;

import java.util.ArrayList;
import java.util.List;

public class AlbumContentActivity extends AppCompatActivity implements ImageAdapterListener {
    private RecyclerView recyclerView;
    private List<String> imageURIs;
    private ImageAdapter imageAdapter;
    private MainController mainController;
    private String albumName;
    private TextView albumNameTxtView;
    private List<Long> image_ids;
    private int album_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_content);

        mainController = new MainController(this);
        handleInteractions();

        image_ids = new ArrayList<>();
        imageURIs = new ArrayList<>();
        albumName = getIntent().getStringExtra("albumName");
        albumNameTxtView = (TextView) findViewById(R.id.albumNameAlbumContent);
        albumNameTxtView.setText(albumName);

        album_id = (int) mainController.getAlbumController().getAlbumIdByName(albumName);
        Log.d("album_id", Integer.toString(album_id));

        image_ids = mainController.getImageAlbumController().getImageIdsByAlbumId(album_id);

        for(Long image_id: image_ids) {
            String ref = mainController.getImageController().getImageRefById(image_id);
            Log.d("ref from id", ref);
            imageURIs.add(ref);
        }

        imageAdapter = new ImageAdapter(this, imageURIs);
        recyclerView = (RecyclerView) this.findViewById(R.id.recyclerViewAlbumContent);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setAdapter(imageAdapter);
        imageAdapter.notifyDataSetChanged();


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
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void getSelectedItemsCount(int count) {
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
        long id = mainController.getImageController().getIdByRef(uri);
        intent.putExtra("id", id);
        intent.putExtra("position", position);
        Log.v("ImageAdapter", "Image selected: " + itemView);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void getInteractedURIs(String uri) {

    }
}
