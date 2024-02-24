package com.example.albumgallery.view;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.albumgallery.R;
import com.example.albumgallery.model.ImageModel;
import com.example.albumgallery.view.adapter.ImageAdapter;
import java.util.List;

public class HomeScreen extends AppCompatActivity {
    private RecyclerView recyclerMediaView;
    private List<String> imagePaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        imagePaths = ImageModel.getAllImages(this);

        recyclerMediaView = findViewById(R.id.recyclerMediaView);
        recyclerMediaView.setLayoutManager(new GridLayoutManager(this, 3));

        ImageAdapter imageAdapter = new ImageAdapter(this, imagePaths);
        recyclerMediaView.setAdapter(imageAdapter);

        if(imageAdapter.getItemCount() == 0){
            Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Images found", Toast.LENGTH_SHORT).show();
        }


        ImageButton btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    // function to open camera on Emulator

}
