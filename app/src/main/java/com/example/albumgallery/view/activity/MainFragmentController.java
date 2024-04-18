package com.example.albumgallery.view.activity;

import static com.example.albumgallery.utils.Constant.REQUEST_CODE_PICK_MULTIPLE_IMAGES;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.databinding.ActivityFragmentControllerBinding;
import com.example.albumgallery.helper.SharePreferenceHelper;
import com.example.albumgallery.presentations.bin.BinFragment;
import com.example.albumgallery.presentations.user.UserActivity;
import com.example.albumgallery.view.fragment.AlbumsMainFragment;
import com.example.albumgallery.view.fragment.FavoriteFragment;
import com.example.albumgallery.view.fragment.HomeScreenFragment;
import com.example.albumgallery.view.listeners.BackgroundProcessingCallback;
import com.example.albumgallery.view.listeners.FragToActivityListener;
import com.example.albumgallery.view.listeners.ImageAdapterListener;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Arrays;


public class MainFragmentController extends AppCompatActivity implements BackgroundProcessingCallback, ImageAdapterListener, FragToActivityListener {
    private static final int CAMERA_REQUEST_CODE = 1000;
    ActivityFragmentControllerBinding binding;
    private boolean isBackgroundTaskCompleted = true;
    private ArrayList<Fragment> fragments = new ArrayList<>(Arrays.asList(new HomeScreenFragment(), new AlbumsMainFragment(), new FavoriteFragment(), new BinFragment()));
    private Fragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        darkMode();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragments.get(0))
                .commit();

        binding = ActivityFragmentControllerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        String fragmentToLoad = getIntent().getStringExtra("fragmentToLoad");
        if (fragmentToLoad != null && fragmentToLoad.equals("AlbumMain")) {
            replaceFragment(fragments.get(1));
        } else if (fragmentToLoad != null && fragmentToLoad.equals("HomeScreen")) {
            replaceFragment(fragments.get(0));
        } else if (fragmentToLoad != null && fragmentToLoad.equals("Bin")) {
            replaceFragment(fragments.get(3));
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.photos) {
                replaceFragment(fragments.get(0));
            } else if (itemId == R.id.albums) {
                replaceFragment(fragments.get(1));
            } else if (itemId == R.id.favorites) {
                replaceFragment(fragments.get(2));
            } else if (itemId == R.id.bin) {
                replaceFragment(fragments.get(3));
            }
            return true;
        });

        MainController mainController = new MainController(this);
        mainController.getImageController().loadFromFirestore();
        mainController.getUserController().loadFromFirestore();
        initiateVariable(mainController.getUserController().getUser().getPicture());
    }

    private void initiateVariable(String picture) {
        ShapeableImageView avatar = findViewById(R.id.action_user);
        if (picture != null) {
            Log.v("UserActivity", "Picture: " + picture);
            Glide.with(this).asBitmap()
                    .load(picture)
                    .addListener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Bitmap> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, com.bumptech.glide.request.target.Target<Bitmap> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            avatar.setImageBitmap(resource);
                            return false;
                        }
                    }).submit();
        }
    }

    private void darkMode() {
        boolean isDarkMode = SharePreferenceHelper.isDarkModeEnabled(this);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        super.onResume();
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (currentFragment instanceof HomeScreenFragment) {
            if (isBackgroundTaskCompleted)
                ((HomeScreenFragment) currentFragment).updateUI();
        } else if (currentFragment instanceof FavoriteFragment) {
//            if (isBackgroundTaskCompleted)
//                ((FavoriteFragment)currentFragment).updateUI();
        } else if (currentFragment instanceof BinFragment) {
            if (isBackgroundTaskCompleted)
                ((BinFragment) currentFragment).updateUI();
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

        currentFragment = fragment;
    }

    @Override
    public void onBackgroundTaskCompleted() {
        isBackgroundTaskCompleted = true;
        Log.v("MainFragmentController", "Background task completed");
        HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.isAdded()) {
            Log.v("MainFragmentController", "Fragment: " + fragment);
            fragment.updateUI();
        }
//        else if (currentFragment instanceof BinFragment) {
//            BinFragment fragment = (BinFragment) currentFragment;
//            fragment.updateUI();
//        }
        isBackgroundTaskCompleted = false;
    }


//    @Override
//    public void getSelectedItemsCount(int count) {
//        ((TextView)findViewById(R.id.numberOfSelectedImages)).setText(count + " items selected");
//        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
//
//        if (currentFragment instanceof HomeScreenFragment) {
//            Log.d("home fragment", "ok");
//            ((HomeScreenFragment) currentFragment).getSelectedItemsCount(count);
//        }
//        else if (currentFragment instanceof BinFragment) {
//            Log.d("bin fragment", "ok");
//            ((BinFragment) currentFragment).getSelectedItemsCount(count);
//        }
//    }

    @Override
    public void getSelectedItemsCount() {

    }

    @Override
    public void handleImagePick(View itemView, String uri, int position) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Log.v("MainFragmentController", "Current fragment: " + currentFragment);
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
        } else if (currentFragment instanceof BinFragment) {
            BinFragment binFragment = (BinFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (binFragment != null) {
                binFragment.handleDeletedImagePick(itemView, uri, position);
            }
        }
    }

    @Override
    public void getInteractedURIs(String uri) {

    }

    /**
     * This method is used to toggle multiple choice mode in the fragment
     */
    @Override
    public void toggleMultipleChoice() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof HomeScreenFragment) {
            Log.v("MainFragmentController", "Toggle multiple choice");
            changeBottomMenu(((HomeScreenFragment) fragment).toggleMultipleChoice());
        } else if (fragment instanceof BinFragment) {
            changeBottomMenu(((BinFragment) fragment).toggleMultipleChoice());
        }
    }

    private void changeBottomMenu(boolean isMultipleChoiceEnabled) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        Log.v("MainFragmentController", "Change bottom menu" + fragment);
        if (isMultipleChoiceEnabled) {
            if (fragment instanceof HomeScreenFragment) {
                Log.v("MainFragmentController", "HomeScreenFragment");
                binding.bottomNavigationView.setVisibility(View.GONE);
                findViewById(R.id.bottomMenuMain1).setVisibility(View.VISIBLE);
                findViewById(R.id.bottomMenuMain2).setVisibility(View.GONE);
            } else if (fragment instanceof BinFragment) {
                Log.v("MainFragmentController", "BinFragment");
                binding.bottomNavigationView.setVisibility(View.GONE);
                findViewById(R.id.bottomMenuMain1).setVisibility(View.GONE);
                findViewById(R.id.bottomMenuMain2).setVisibility(View.VISIBLE);
            }
        } else {
            binding.bottomNavigationView.setVisibility(View.VISIBLE);
            findViewById(R.id.bottomMenuMain1).setVisibility(View.GONE);
            findViewById(R.id.bottomMenuMain2).setVisibility(View.GONE);
        }
    }

    public void shareAction(View view) {
        HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.isAdded()) {
            fragment.ActivityToFragListener("Share");
        } else {
            Log.e("MainFragmentController", "Fragment is null or not added");
        }
    }

    public void addAction(View view) {
    }

    public void likeAction(View view) {
        HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.isAdded()) {
            fragment.ActivityToFragListener("Like");
        } else {
            Log.e("MainFragmentController", "Fragment is null or not added");
        }
    }

    public void deleteAction(View view) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof HomeScreenFragment) {
            ((HomeScreenFragment) fragment).ActivityToFragListener("Delete");
        } else {
            ((BinFragment) fragment).ActivityToFragListener("Delete");
        }
    }

    public void cameraAction(View view) {
        Log.v("MainFragmentController", "Camera action");
        HomeScreenFragment fragment = (HomeScreenFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.isAdded()) {
            fragment.ActivityToFragListener("Camera");
        } else {
            Log.e("MainFragmentController", "Fragment is null or not added");
        }
    }

    public void selectAction(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        this.startActivityForResult(intent, REQUEST_CODE_PICK_MULTIPLE_IMAGES);
    }

    public void userAction(View view) {
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    public void restoreAction(View view) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onFragmentAction(String action, Object data) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        switch (action) {
            case "Share":
                shareImages((ArrayList<Uri>) data);
                break;
            case "Delete":
                boolean isDeleted = (boolean) data;
                Log.v("MainFragmentController", "Is deleted: " + isDeleted);
                if (isDeleted) {
                    toggleMultipleChoice();
                } else {
                    Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show();
                }
                break;
            case "Like":
                boolean isLiked = (boolean) data;
                Log.v("MainFragmentController", "Is liked: " + isLiked);
                if (isLiked) {
                    toggleMultipleChoice();
                } else {
                    Toast.makeText(this, "Failed to like image", Toast.LENGTH_SHORT).show();
                }
                break;
            case "ShowMultipleChoice":
                String count = data.toString();
                if (fragment instanceof HomeScreenFragment || fragment instanceof FavoriteFragment)
                    ((TextView) findViewById(R.id.numberOfSelectedImages)).setText(count);
                else if (fragment instanceof BinFragment) {
                    ((TextView) findViewById(R.id.numberOfSelectedImages2)).setText(count);
                }
                break;
            case "SelectAll":
                toggleMultipleChoice();
                break;
        }
    }

    private void shareImages(ArrayList<Uri> imageUris) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent, "Share images"));
    }

    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}