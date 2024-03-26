package com.example.albumgallery.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.albumgallery.R;

public class AlbumMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_main);

        ImageButton btnCreateAlbum = (ImageButton) findViewById(R.id.btnCreateAlbum);
        btnCreateAlbum.setOnClickListener(view -> {
            Intent intent = new Intent(AlbumMain.this, CreateAlbumActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
