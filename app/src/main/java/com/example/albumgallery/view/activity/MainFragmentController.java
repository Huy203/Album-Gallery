package com.example.albumgallery.view.activity;

import static com.example.albumgallery.utils.Constant.REQUEST_CODE_PICK_MULTIPLE_IMAGES;
import static com.example.albumgallery.utils.Utilities.convertFromBitmapToURI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.albumgallery.R;
import com.example.albumgallery.databinding.ActivityFragmentControllerBinding;
import com.example.albumgallery.view.fragment.AlbumsMainFragment;
import com.example.albumgallery.view.fragment.FavoriteFragment;
import com.example.albumgallery.view.fragment.HomeScreenFragment;
import com.example.albumgallery.view.listeners.BackgroundProcessingCallback;
import com.example.albumgallery.view.listeners.FragToActivityListener;
import com.example.albumgallery.view.listeners.ImageAdapterListener;

import java.util.ArrayList;
import java.util.Arrays;


public class MainFragmentController extends AppCompatActivity implements BackgroundProcessingCallback, ImageAdapterListener, FragToActivityListener {
    private static final int CAMERA_REQUEST_CODE = 1000;
    ActivityFragmentControllerBinding binding;
    private boolean isBackgroundTaskCompleted = true;
    private ArrayList<Fragment> fragments = new ArrayList<>(Arrays.asList(new HomeScreenFragment(), new AlbumsMainFragment(), new FavoriteFragment()));
    private Fragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragments.get(0)).commit();

        binding = ActivityFragmentControllerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        String fragmentToLoad = getIntent().getStringExtra("fragmentToLoad");
        if (fragmentToLoad != null && fragmentToLoad.equals("AlbumMain")) {
            replaceFragment(fragments.get(1));
        } else if (fragmentToLoad != null && fragmentToLoad.equals("HomeScreen")) {
            replaceFragment(fragments.get(0));
        }


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.photos) {
                replaceFragment(fragments.get(0));
            } else if (itemId == R.id.albums) {
                replaceFragment(fragments.get(1));
            } else if (itemId == R.id.favorites) {
                replaceFragment(fragments.get(2));
            }
            return true;
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof HomeScreenFragment && isBackgroundTaskCompleted) {
            HomeScreenFragment fragment = (HomeScreenFragment) currentFragment;
            fragment.updateUI();
        } else {
            Log.v("MainFragmentController", "Fragment is null or background task is not completed");
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
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        currentFragment = fragment;
    }

    @Override
    public void onBackgroundTaskCompleted() {
        HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.updateUI();
        }
    }

    public void getSelectedItemsCount(int count) {
        ((TextView) findViewById(R.id.numberOfSelectedImages)).setText(count + " images selected");
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof HomeScreenFragment) {
            HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment != null) {
                fragment.getSelectedItemsCount(count);
            }
        }
    }

    @Override
    public void handleImagePick(View itemView, String uri, int position) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof HomeScreenFragment) {
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

    @Override
    public void getInteractedURIs(String uri) {

    }

    @Override
    public void toggleMultipleChoice(int length) {
        getSelectedItemsCount(length);
        HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            changeBottomMenu(fragment.toggleMultipleChoice(length));
        }
    }

    private void changeBottomMenu(boolean isMultipleChoiceEnabled) {
        if (isMultipleChoiceEnabled) {
            binding.bottomNavigationView.setVisibility(View.GONE);
            findViewById(R.id.bottomMenuMain).setVisibility(View.VISIBLE);
        } else {
            binding.bottomNavigationView.setVisibility(View.VISIBLE);
            findViewById(R.id.bottomMenuMain).setVisibility(View.GONE);
        }
    }

    public void shareAction(View view) {
        HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.isAdded()) {
            fragment.ActivityToFragListener("Share");
        } else {
            Log.e("MainFragmentController", "Fragment is null or not added");
//        }
//        Glide.with(this)
//                .asBitmap()
//                .load(Uri.parse(imagePaths.get(currentPosition)))
//                .addListener(new RequestListener<Bitmap>() {
//                    @Override
//                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
//                        Log.e("DetailPicture", "Failed to load image: " + e.getMessage());
//                        Toast.makeText(MainFragmentController.this, "Failed to share image", Toast.LENGTH_SHORT).show();
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
//                        shareImageAndText(resource);
//                        return false;
//                    }
//                })
//                .submit();
//    }
        }
    }

    public void addAction(View view) {
    }

    public void likeAction(View view) {
    }

    public void deleteAction(View view) {
        Log.v("MainFragmentController", "Delete action");
        HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.isAdded()) {
            fragment.ActivityToFragListener("Delete");
        } else {
            Log.e("MainFragmentController", "Fragment is null or not added");
        }
    }

    public void cameraAction(View view){
        Log.v("MainFragmentController", "Camera action");
        HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.isAdded()) {
            fragment.ActivityToFragListener("Camera");
        } else {
            Log.e("MainFragmentController", "Fragment is null or not added");
        }
    }

    public void selectAction(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        this.startActivityForResult(intent, REQUEST_CODE_PICK_MULTIPLE_IMAGES);
    }

    @Override
    public void onFragmentAction(String action, Object data) {

    }

    private void shareImageAndText(Bitmap bitmap, Context context) {
        Uri uri = convertFromBitmapToURI(this, bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);

        // putting uri of image to be shared
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        // adding text to share
        intent.putExtra(Intent.EXTRA_TEXT, "Sharing Image");

        // Add subject Here
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");

        // setting type to image
        intent.setType("image/png");

        // calling startactivity() to share
        startActivity(Intent.createChooser(intent, "Share Via"));
    }
}