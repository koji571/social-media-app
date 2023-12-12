package com.example.eco2;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText usernameEditText, nameEditText;
    private String currentUserId;
    private StorageReference storageReference;
    private Uri selectedImageUri = null; // Uri for the selected image
    public static final String PROFILE_UPDATED = "com.example.eco2.PROFILE_UPDATED";

    ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        profileImageView.setImageURI(selectedImageUri);
                    }
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        profileImageView = findViewById(R.id.profile_image);
        usernameEditText = findViewById(R.id.username);
        nameEditText = findViewById(R.id.name);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
            loadUserProfile(currentUserId);
        }

        profileImageView.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare() // Crop image(Optional)
                    .compress(512) // Final image size
                    .maxResultSize(512, 512)
                    .createIntent(intent -> {
                        imagePickerLauncher.launch(intent);
                        return null;
                    });
        });
    }

    private void loadUserProfile(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String imageUrl = dataSnapshot.child("imageurl").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);

                    if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("default")) {
                        Glide.with(EditProfileActivity.this).load(imageUrl).into(profileImageView);
                    }
                    usernameEditText.setText(username);
                    nameEditText.setText(name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
            }
        });
    }
    public void finalizeRegistration(View view) {
        String newUsername = usernameEditText.getText().toString().trim();
        String newName = nameEditText.getText().toString().trim();

        if (newUsername.isEmpty() || newName.isEmpty()) {
            Toast.makeText(this, "Username and name cannot be empty.", Toast.LENGTH_LONG).show();
            return;
        }

        updateProfile(newUsername, newName, selectedImageUri);

        // Send broadcast
        Intent intent = new Intent(MainActivity.ACTION_PROFILE_UPDATED);
        sendBroadcast(intent);

        finish();
    }

    // Method to update profile in Firebase
    public void updateProfile(String username, String name, Uri profilePictureUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

            // Update username and name
            Map<String, Object> updates = new HashMap<>();
            updates.put("username", username);
            updates.put("name", name);
            userRef.updateChildren(updates);

            // Only upload new profile picture if a new one has been selected
            if (profilePictureUri != null) {
                StorageReference profilePicRef = storageReference.child("profile_pics/" + currentUser.getUid() + ".jpg");
                profilePicRef.putFile(profilePictureUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        userRef.child("imageurl").setValue(uri.toString());
                                    }
                                });
                            }
                        });
            }
        }
    }
    public void backProfile(View view){
        finish();
    }
}