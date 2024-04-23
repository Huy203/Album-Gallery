package com.example.albumgallery.presentations.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.helper.SharePreferenceHelper;
import com.example.albumgallery.model.auth.UserModel;
import com.example.albumgallery.view.activity.Auth;
import com.example.albumgallery.view.listeners.FragToActivityListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserActivity extends AppCompatActivity implements FragToActivityListener {
    private boolean isDarkMode;
    private MainController mainController;
    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mainController = new MainController(this);
        isDarkMode = SharePreferenceHelper.isDarkModeEnabled(this);
        darkMode();

        userModel = getUser();
        initializeVariables();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    }

    public void darkMode() {
        SwitchMaterial switchMaterial = findViewById(R.id.darkModeSwitch);
        if (isDarkMode) {
            switchMaterial.setChecked(true);
        }
    }

    private void initializeVariables() {
        ((MaterialButton) findViewById(R.id.languageTextView)).setText(AppCompatDelegate.getApplicationLocales().toLanguageTags());

        if (userModel == null) {
            Intent intent = new Intent(UserActivity.this, Auth.class);
            startActivity(intent);
            finish();
        } else {
            MaterialTextView userName = findViewById(R.id.usernameTextView);
            userName.setText(userModel.getUsername());

            MaterialTextView emailTextView = findViewById(R.id.emailTextView);
            emailTextView.setText(userModel.getEmail());

            MaterialTextView dateOfBirthTextView = findViewById(R.id.dateOfBirthTextView);
            dateOfBirthTextView.setText(userModel.getBirth());

            MaterialTextView phoneTextView = findViewById(R.id.phoneTextView);
            phoneTextView.setText(userModel.getPhone());

            String picture = userModel.getPicture();
            ShapeableImageView avatar = findViewById(R.id.avatarImageView);
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
        Log.v("UserActivity", "isDarkMode: " + isDarkMode);
    }

    public void darkModeAction(View view) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            SharePreferenceHelper.setDarkModeEnabled(this, false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            SharePreferenceHelper.setDarkModeEnabled(this, true);
        }
    }

    public UserModel getUser() {
        return mainController.getUserController().getUser();
    }

    public void editAction(View view) {
        DialogFragment dialogFragment = new UserEditFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, dialogFragment)
                .addToBackStack(null).commit();
    }

    public void backAction(View view) {
        finish();
    }

    public void signOutAction(View view) {
        mainController.getUserController().getFirebaseManager().getFirebaseAuth().signOut();
        mainController.getImageController().delete(null);
        mainController.getAlbumController().delete(null);
        mainController.getImageAlbumController().delete(null);
        mainController.getUserController().delete(null);

    }

    public void languageAction(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "English (en)");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "Vietnamese (vi)");
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case 0:
                    // Handle selection of English (en)
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"));
                    recreate();
                    return true;
                case 1:
                    // Handle selection of Vietnamese (vi)
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("vi"));
                    recreate();
                    return true;
                default:
                    return false;
            }
        });
        popupMenu.show();
    }

    public void changeAvatarAction(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onFragmentAction(String action, Object data) {
        if (action.equals("update")) {
            updateUI();
        }
    }

    private void updateUI() {
        userModel = getUser();
        initializeVariables();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // Get the selected image URI
            Uri imageUri = data.getData();
            Task<Uri> uploadTask = mainController.getImageController().uploadImage(imageUri);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Log.v("UserActivity", "Download URI: " + downloadUri);
                    mainController.getUserController().update("picture", downloadUri.toString(), "id = '" + userModel.getId() + "'");
                    updateUI();
                }
            });
        }
    }
}