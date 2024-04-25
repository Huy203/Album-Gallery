package com.example.albumgallery.view.activity;

import android.os.Bundle;

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
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_slide_show);

        ImageSlider imageSlider = findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        // Retrieve the imageURIs ArrayList from the intent extras
        imageURIs = getIntent().getStringArrayListExtra("imageURIs");

        for (String uri : imageURIs) {
            slideModels.add(new SlideModel(uri, ScaleTypes.FIT));
        }
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        handleInteractions();
    }

    private void handleInteractions() {
        ExtendedFloatingActionButton backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(view -> {
            // Start the MainFragmentController activity
            finish();
        });
    }
}

