package com.example.albumgallery.view.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.albumgallery.FirebaseManager;
import com.example.albumgallery.R;
import com.example.albumgallery.controller.MainController;
import com.example.albumgallery.helper.SharePreferenceHelper;
import com.example.albumgallery.model.auth.UserModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Date;
import java.util.Objects;

public class Auth extends AppCompatActivity {
    ProgressDialog loadingBar;
    private FirebaseManager firebaseManager;
    private MainController mainController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mainController = new MainController(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseManager = FirebaseManager.getInstance(this);
    }

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(40, 20, 40, 20);

        TextInputEditText emailEditText = new TextInputEditText(this);
        emailEditText.setHint("Enter your email");
        LinearLayout.LayoutParams emailParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        emailParams.bottomMargin = 20;
        emailEditText.setLayoutParams(emailParams);

        dialogLayout.addView(emailEditText);
        builder.setView(dialogLayout);

        builder.setPositiveButton("Recover", (dialog, which) -> {
            String email = emailEditText.getText().toString().trim();
            beginRecovery(email);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue_700));
            if (SharePreferenceHelper.isDarkModeEnabled(Auth.this)) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.white));
            } else {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
            }
        });

        dialog.show();

    }

    private void beginRecovery(String email) {
        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Sending Email....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // Gửi email khôi phục mật khẩu
        firebaseManager.getFirebaseAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        loadingBar.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Password reset email sent", Snackbar.LENGTH_LONG)
                                .setTextColor(SharePreferenceHelper.isDarkModeEnabled(this) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black))
                                .setBackgroundTint(SharePreferenceHelper.isDarkModeEnabled(this) ? getResources().getColor(R.color.black) : getResources().getColor(R.color.white))
                                .show();
                    }
                });
    }

    public void signInAction(View view) {
        String email = ((TextInputEditText) findViewById(R.id.emailOrUsernameEditText)).getText().toString();
        String password = ((TextInputEditText) findViewById(R.id.passwords)).getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), "Please fill in all fields", Snackbar.LENGTH_LONG)
                    .setTextColor(SharePreferenceHelper.isDarkModeEnabled(this) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black))
                    .show();
            return;
        }

        firebaseManager.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), "The supplied auth credential is incorrect", Snackbar.LENGTH_LONG)
                                .setTextColor(SharePreferenceHelper.isDarkModeEnabled(Auth.this) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black))
                                .show();
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content), "The supplied auth credential is incorrect", Snackbar.LENGTH_LONG)
                            .setTextColor(SharePreferenceHelper.isDarkModeEnabled(Auth.this) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black))
                            .show();
                });
    }

    public void signUpAction(View view) {
        String email = ((TextInputEditText) findViewById(R.id.email)).getText().toString();
        String username = ((TextInputEditText) findViewById(R.id.userName)).getText().toString();
        String phone = ((TextInputEditText) findViewById(R.id.phoneNumber)).getText().toString();
        String dateOfBirth = ((TextInputEditText) findViewById(R.id.dateOfBirth)).getText().toString();
        String password = ((TextInputEditText) findViewById(R.id.password)).getText().toString();
        String confirmPassword = ((TextInputEditText) findViewById(R.id.confirmPassword)).getText().toString();

        if (email.isEmpty() || username.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(Auth.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(Auth.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        firebaseManager.getFirebaseAuth().fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                        if (isNewUser) {
                            createNewUser(email, password, username, phone, dateOfBirth);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), "User already exists", Snackbar.LENGTH_LONG)
                                    .setTextColor(SharePreferenceHelper.isDarkModeEnabled(Auth.this) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black))
                                    .show();
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            Snackbar.make(findViewById(android.R.id.content), e.getMessage(), Snackbar.LENGTH_LONG)
                                    .setTextColor(SharePreferenceHelper.isDarkModeEnabled(Auth.this) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black))
                                    .show();
                        }
                    }
                });
    }

    public void createNewUser(String email, String password, String username, String phone, String dateOfBirth) {
        firebaseManager.getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        signInLayoutAction(findViewById(R.id.signIn));
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = firebaseManager.getFirebaseAuth().getCurrentUser();
                        if (user != null) {
                            Uri imageUri = Uri.parse("android.resource://com.example.albumgallery/drawable/blank_profile_picture");

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .setPhotoUri(imageUri)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.v("Firebase", "User profile updated.");
                                        }
                                    });
                            String created_at = new Date(Objects.requireNonNull(user.getMetadata()).getCreationTimestamp()).toString();
                            mainController.getUserController().insert(new UserModel(user.getUid(), username, email, phone, dateOfBirth, created_at, "android.resource://com.example.albumgallery/drawable/blank_profile_picture"));
                            Log.v("Firebase", mainController.getUserController().getUser().getPicture());
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Snackbar.make(findViewById(android.R.id.content), "Create user failed", Snackbar.LENGTH_LONG)
                                .setTextColor(SharePreferenceHelper.isDarkModeEnabled(Auth.this) ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black))
                                .show();
                    }
                });
    }

    public void signInLayoutAction(View view) {
        findViewById(R.id.signUp).setBackground(null);
        ((TextView) findViewById(R.id.signUp)).setTextColor(getResources().getColor(R.color.blue_500, null));
        view.setBackground(getResources().getDrawable(R.drawable.switch_trcks, null));
        findViewById(R.id.signUpLayout).setVisibility(View.GONE);
        findViewById(R.id.signInLayout).setVisibility(View.VISIBLE);
        ((TextView) view).setTextColor(getResources().getColor(R.color.textColor, null));
    }

    public void signUpLayoutAction(View view) {
        view.setBackground(getResources().getDrawable(R.drawable.switch_trcks, null));
        ((TextView) view).setTextColor(getResources().getColor(R.color.textColor, null));
        findViewById(R.id.signIn).setBackground(null);
        findViewById(R.id.signUpLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.signInLayout).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.signIn)).setTextColor(getResources().getColor(R.color.blue_500, null));
    }

    public void forgetPasswordAction(View view) {
        showRecoverPasswordDialog();
    }
}