package com.example.albumgallery.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.databinding.ActivityFragmentControllerBinding;
import com.example.albumgallery.view.adapter.ImageAdapterListener;
import com.example.albumgallery.view.fragment.AlbumsMainFragment;
import com.example.albumgallery.view.fragment.FavoriteFragment;
import com.example.albumgallery.view.fragment.HomeScreenFragment;

import java.util.ArrayList;
import java.util.List;


public class MainFragmentController extends AppCompatActivity implements BackgroundProcessingCallback, ImageAdapterListener {
    ActivityFragmentControllerBinding binding;
    private boolean isBackgroundTaskCompleted = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeScreenFragment fragment = new HomeScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();

        binding = ActivityFragmentControllerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // fragment đầu tiên khi vừa vào app
//        replaceFragment(new HomeScreenFragment());
        String fragmentToLoad = getIntent().getStringExtra("fragmentToLoad");
        if(fragmentToLoad != null && fragmentToLoad.equals("AlbumMain")) {
            replaceFragment(new AlbumsMainFragment());
        } else if (fragmentToLoad != null && fragmentToLoad.equals("HomeScreen")) {
            replaceFragment(new HomeScreenFragment());
        }


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.photos) {
                replaceFragment(new HomeScreenFragment());
            } else if (itemId == R.id.albums) {
                replaceFragment(new AlbumsMainFragment());
            } else if (itemId == R.id.favorites) {
                replaceFragment(new FavoriteFragment());
            }
            return true;
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        super.onResume();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        HomeScreenFragment fragment = null;
        if(currentFragment instanceof HomeScreenFragment) {
            fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }
        if (fragment != null) {
            if (isBackgroundTaskCompleted)
                fragment.updateUI();
        } else {
            Log.v("MainFragmentController", "fragment is null");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (data.getData() == null) {
                Log.d("Check data", "is null");
            } else {
                Log.d("Check data", "is not null");
            }
        }
        HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackgroundTaskCompleted() {
        isBackgroundTaskCompleted = true;
        HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.updateUI();
        }
        isBackgroundTaskCompleted = false;
    }

    @Override
    public void getSelectedItemsCount(int count) {
        HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.getSelectedItemsCount(count);
        }
    }

    @Override
    public void handleImagePick(View itemView, String uri, int position) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(currentFragment instanceof HomeScreenFragment) {
            HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment != null) {
                fragment.handleImagePick(itemView, uri, position);
            }
        } else if (currentFragment instanceof FavoriteFragment) {
            FavoriteFragment favoriteFragment = (FavoriteFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (favoriteFragment != null) {
                favoriteFragment.handleImagePick(itemView, uri, position);
            }
        }
    }
}