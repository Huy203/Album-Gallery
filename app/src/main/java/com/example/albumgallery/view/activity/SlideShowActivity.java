package com.example.albumgallery.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.albumgallery.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class SlideShowActivity extends AppCompatActivity {
    private ArrayList<String> imageURIs;
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_slide_show);

        ImageSlider imageSlider = findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        // Retrieve the imageURIs ArrayList from the intent extras
        imageURIs = getIntent().getStringArrayListExtra("imageURIs");
        Log.d("SlideShowActivity", "Number of image URIs: " + imageURIs.size());

        for (String uri : imageURIs) {
            slideModels.add(new SlideModel(uri, ScaleTypes.FIT));
            // slideModels.add(new SlideModel(R.drawable.album, ScaleTypes.FIT));
        }
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        handleInteractions();
    }
    private void handleInteractions(){
        ExtendedFloatingActionButton backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the MainFragmentController activity
                finish();
            }
        });
    }
}

