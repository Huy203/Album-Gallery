package com.example.albumgallery.model.auth;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AuthenticationManager {
    // AuthenticationManager class for authentication
    private Credentials credentials;
    private FirebaseAuth mAuth;

    //private UserModel user;

    public AuthenticationManager(Credentials credentials) {
        this.credentials = credentials;
        mAuth = FirebaseAuth.getInstance();
    }
    public void checkEmailAndUsernameExist(String email, String username, OnCheckExistListener listener) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean emailExist = dataSnapshot.exists();

                usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean usernameExist = dataSnapshot.exists();

                        // Gửi kết quả về cho người nghe
                        listener.onExist(emailExist, usernameExist);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Xử lý lỗi nếu cần
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    public void login() {
        // Login the user
    }

    public void logout() {
        // Logout the user
    }

    public void register(UserModel user, final OnRegisterListener listener) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Create a reference to the root directory of Firebase Storage
                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                            // Create a reference to the user's directory
                            StorageReference userRef = storageRef.child(userId);

                            // Create a reference to the images directory inside the user's directory
                            StorageReference imagesRef = userRef.child("images/");

                            // Create a placeholder file inside the images directory
                            imagesRef.child(".placeholder").putBytes(new byte[0]) // Put an empty byte array to create the placeholder file
                                    .addOnSuccessListener(taskSnapshot -> {
                                        // Placeholder file created successfully
                                        // Proceed to store user's information in the Realtime Database
                                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                                        usersRef.setValue(user);

                                        // Notify the listener about successful registration
                                        listener.onSuccess(userId);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Failed to create placeholder file
                                        listener.onFailure("Failed to create images directory: " + e.getMessage());
                                    });
                        } else {
                            listener.onFailure("Failed to get user information.");
                        }
                    } else {
                        listener.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void resetPassword() {
        // Reset the user's password
    }

    public void changePassword() {
        // Change the user's password
    }

    public interface OnRegisterListener {
        void onSuccess(String userId);
        void onFailure(String errorMessage);
    }

    public interface OnCheckExistListener {
        void onExist(boolean emailExist, boolean usernameExist);
    }
    public void loginWithEmailAndPassword(String emailOrUsername, String password, OnLoginListener listener) {
        // Kiểm tra xem chuỗi đầu vào có phải là email hay không
        if (isValidEmail(emailOrUsername)) {
            // Đăng nhập bằng email
            mAuth.signInWithEmailAndPassword(emailOrUsername, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công, lấy thông tin người dùng hiện tại
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Lấy ID của người dùng đã đăng nhập
                                String userId = firebaseUser.getUid();
                                // Gửi kết quả về cho người nghe
                                listener.onSuccess(userId);
                            } else {
                                // Không thể lấy thông tin người dùng hiện tại
                                listener.onFailure("Failed to get user information.");
                            }
                        } else {
                            // Đăng nhập thất bại, kiểm tra nguyên nhân
                            Exception exception = task.getException();
                            if (exception instanceof FirebaseAuthInvalidUserException) {
                                // Người dùng không tồn tại hoặc đã bị vô hiệu hóa
                                listener.onFailure("User does not exist or is disabled.");
                            } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                // Sai mật khẩu
                                listener.onFailure("Incorrect password.");
                            } else {
                                // Lỗi khác
                                listener.onFailure(exception.getMessage());
                            }
                        }
                    });
        } else {
            // Đăng nhập bằng tên người dùng (username)
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
            usersRef.orderByChild("username").equalTo(emailOrUsername).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Tìm thấy username trong cơ sở dữ liệu, lấy email tương ứng
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserModel user = snapshot.getValue(UserModel.class);
                            String email = user.getEmail();

                            mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Đăng nhập thành công, lấy thông tin người dùng hiện tại
                                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                            if (firebaseUser != null) {
                                                // Lấy ID của người dùng đã đăng nhập
                                                String userId = firebaseUser.getUid();
                                                // Gửi kết quả về cho người nghe
                                                listener.onSuccess(userId);
                                            } else {
                                                listener.onFailure("Failed to get user information.");
                                            }
                                        } else {
                                            // Đăng nhập thất bại, kiểm tra nguyên nhân
                                            Exception exception = task.getException();
                                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                                // Sai mật khẩu
                                                listener.onFailure("Incorrect password.");
                                            } else {
                                                // Lỗi khác
                                                listener.onFailure(exception.getMessage());
                                            }
                                        }
                                    });
                        }
                    } else {
                        // Không tìm thấy username trong cơ sở dữ liệu
                        listener.onFailure("Username not found.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi nếu cần
                }
            });
        }
    }

    // Phương thức kiểm tra chuỗi có phải là email hay không
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void sendPasswordResetEmail(String email, OnCompleteListener<Void> onCompleteListener) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(onCompleteListener);
    }

    public interface OnLoginListener {
        void onSuccess(String userId);
        void onFailure(String errorMessage);
    }

    public interface OnLogoutListener {
        void onSuccess(); // Đăng xuất thành công
        void onFailure(String errorMessage); // Đăng xuất thất bại
    }

    // Phương thức đăng xuất người dùng
    public void signOut(OnLogoutListener listener) {
        mAuth.signOut(); // Gọi phương thức signOut() của FirebaseAuth

        // Kiểm tra xem người dùng đã đăng xuất thành công hay không
        if (mAuth.getCurrentUser() == null) {
            // Đăng xuất thành công, gọi phương thức onSuccess của listener
            listener.onSuccess();
        } else {
            // Đăng xuất thất bại, gọi phương thức onFailure của listener
            listener.onFailure("Logout failed: User is still logged in.");
        }
    }
}

