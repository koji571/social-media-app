package com.example.eco2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.eco2.AppFragments.RegisterUserDetailsFragment;
import com.example.eco2.AppFragments.UploadProfilePictureFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private String username, name, email, password;
    private Uri profilePictureUri;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.register_page);

        pd = new ProgressDialog(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RegisterUserDetailsFragment())
                    .commit();
        }
    }

    public void toProfilePictureFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new UploadProfilePictureFragment())
                .addToBackStack(null)
                .commit();
    }

    public void onLoginClick(View view) {
        finish();
    }

    public void storeUserDetails(String username, String name, String email, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void setProfilePictureUri(Uri uri) {
        this.profilePictureUri = uri;
    }

    public void finalizeRegistration(View view) {
        pd.setMessage("Registering User...");
        pd.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> uploadProfilePictureAndUserData())
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    showToast("Registration failed: " + e.getMessage());
                });
    }

    private void uploadProfilePictureAndUserData() {
        if (profilePictureUri == null) {
            pd.dismiss();
            showToast("Profile picture not selected.");
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_pics/" + mAuth.getCurrentUser().getUid());
        storageRef.putFile(profilePictureUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    uploadUserData(imageUrl);
                }))
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    showToast("Failed to upload profile picture: " + e.getMessage());
                });
    }

    private void uploadUserData(String imageUrl) {
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("username", username);
        userData.put("imageurl", imageUrl);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid())
                .setValue(userData)
                .addOnCompleteListener(task -> {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        navigateToMainActivity();
                    } else {
                        showToast("Failed to upload user data: " + task.getException().getMessage());
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToMainActivity() {
        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
