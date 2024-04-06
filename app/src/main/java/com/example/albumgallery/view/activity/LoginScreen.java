package com.example.albumgallery.view.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.albumgallery.R;
import com.example.albumgallery.model.auth.AuthenticationManager;
import com.example.albumgallery.model.auth.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginScreen extends AppCompatActivity {
    private AuthenticationManager authManager;
    ProgressDialog loadingBar;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);

        authManager = new AuthenticationManager(null);

        findViewById(R.id.forgetPass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });

        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackground(getResources().getDrawable(R.drawable.switch_trcks, null));
                ((TextView) v).setTextColor(getResources().getColor(R.color.textColor, null));
                findViewById(R.id.logIn).setBackground(null);
                findViewById(R.id.singUpLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.logInLayout).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.logIn)).setTextColor(getResources().getColor(R.color.pinkColor, null));

                String email = ((TextInputEditText) findViewById(R.id.eMails)).getText().toString();
                String username = ((TextInputEditText) findViewById(R.id.userName)).getText().toString();
                String phone = ((TextInputEditText) findViewById(R.id.phoneNumber)).getText().toString();
                String address = ((TextInputEditText) findViewById(R.id.address)).getText().toString();
                String password = ((TextInputEditText) findViewById(R.id.passwordss)).getText().toString();

                if (email.isEmpty() || username.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginScreen.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserModel userModel = new UserModel(username, password, email, phone, address);

                // Gọi phương thức register của AuthenticationManager để lưu thông tin người dùng vào Firebase
                authManager.register(userModel, new AuthenticationManager.OnRegisterListener() {
                    @Override
                    public void onSuccess(String userId) {
                        Toast.makeText(LoginScreen.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                        // Xử lý khi đăng ký thành công, ví dụ: chuyển sang màn hình đăng nhập
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(LoginScreen.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        findViewById(R.id.signUpButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((TextInputEditText) findViewById(R.id.eMails)).getText().toString();
                String username = ((TextInputEditText) findViewById(R.id.userName)).getText().toString();
                String phone = ((TextInputEditText) findViewById(R.id.phoneNumber)).getText().toString();
                String address = ((TextInputEditText) findViewById(R.id.address)).getText().toString();
                String password = ((TextInputEditText) findViewById(R.id.passwordss)).getText().toString();

                if (email.isEmpty() || username.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginScreen.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra xem email hoặc username đã tồn tại chưa
                authManager.checkEmailAndUsernameExist(email, username, new AuthenticationManager.OnCheckExistListener() {
                    @Override
                    public void onExist(boolean emailExist, boolean usernameExist) {
                        if (emailExist || usernameExist) {
                            // Email hoặc username đã tồn tại, hiển thị thông báo lỗi
                            if (emailExist && usernameExist) {
                                Toast.makeText(LoginScreen.this, "Email and username already exist", Toast.LENGTH_SHORT).show();
                            } else if (emailExist) {
                                Toast.makeText(LoginScreen.this, "Email already exists", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginScreen.this, "Username already exists", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Email và username chưa tồn tại, tiến hành đăng ký
                            UserModel userModel = new UserModel(username, password, email, phone, address);

                            authManager.register(userModel, new AuthenticationManager.OnRegisterListener() {
                                @Override
                                public void onSuccess(String userId) {
                                    Toast.makeText(LoginScreen.this, "Registered successfully!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(LoginScreen.this, LoginScreen.class);
                                    startActivity(intent);

                                    // Đặt lại giao diện sau khi đăng ký thành công
                                    findViewById(R.id.signUp).setBackground(null);
                                    ((TextView) findViewById(R.id.logIn)).setTextColor(getResources().getColor(R.color.pinkColor, null));
                                    findViewById(R.id.singUpLayout).setVisibility(View.GONE);
                                    findViewById(R.id.logInLayout).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.logIn)).setTextColor(getResources().getColor(R.color.textColor, null));

                                    // Xóa các trường nhập liệu
                                    ((TextInputEditText) findViewById(R.id.eMails)).setText("");
                                    ((TextInputEditText) findViewById(R.id.userName)).setText("");
                                    ((TextInputEditText) findViewById(R.id.phoneNumber)).setText("");
                                    ((TextInputEditText) findViewById(R.id.address)).setText("");
                                    ((TextInputEditText) findViewById(R.id.passwordss)).setText("");
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(LoginScreen.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });

        findViewById(R.id.logIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.signUp).setBackground(null);
                ((TextView) findViewById(R.id.signUp)).setTextColor(getResources().getColor(R.color.pinkColor, null));
                v.setBackground(getResources().getDrawable(R.drawable.switch_trcks, null));
                findViewById(R.id.singUpLayout).setVisibility(View.GONE);
                findViewById(R.id.logInLayout).setVisibility(View.VISIBLE);
                ((TextView) v).setTextColor(getResources().getColor(R.color.textColor, null));
            }
        });

        findViewById(R.id.singIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailOrUsername = ((TextInputEditText) findViewById(R.id.emailOrUsernameEditText)).getText().toString();
                String password = ((TextInputEditText) findViewById(R.id.passwords)).getText().toString();

                if (emailOrUsername.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginScreen.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                authManager.loginWithEmailAndPassword(emailOrUsername, password, new AuthenticationManager.OnLoginListener() {
                    @Override
                    public void onSuccess(String userId) {
                        // Đăng nhập thành công, chuyển sang màn hình chính hoặc màn hình khác
                        Toast.makeText(LoginScreen.this, "Login successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginScreen.this, RegisterScreen.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // Đăng nhập thất bại, hiển thị thông báo lỗi
                        Toast.makeText(LoginScreen.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(40, 20, 40, 20); // Đặt padding cho dialog

        final EditText emailEditText = new EditText(this);
        emailEditText.setHint("Enter your email");
        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEditText.setTextColor(getResources().getColor(R.color.black));
        LinearLayout.LayoutParams emailParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        emailParams.bottomMargin = 20; // Đặt margin dưới cho EditText
        emailEditText.setLayoutParams(emailParams);

        dialogLayout.addView(emailEditText);

        builder.setView(dialogLayout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = emailEditText.getText().toString().trim();
                beginRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

// Tạo và hiển thị dialog
        AlertDialog dialog = builder.create();

// Thiết lập màu cho các nút trong dialog
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red_200));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red_700));
            }
        });

        dialog.show();

    }

    private void beginRecovery(String email) {
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Sending Email....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // Gửi email khôi phục mật khẩu
        authManager.sendPasswordResetEmail(email, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if (task.isSuccessful()) {
                    // Nếu gửi email thành công
                    Toast.makeText(LoginScreen.this, "Done sent", Toast.LENGTH_LONG).show();
                } else {
                    // Nếu gặp lỗi khi gửi email
                    Toast.makeText(LoginScreen.this, "Error Occurred: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}