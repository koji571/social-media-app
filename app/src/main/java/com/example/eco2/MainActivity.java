package com.example.eco2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.eco2.AppFragments.HomeFragment;
import com.example.eco2.AppFragments.NotificationFragment;
import com.example.eco2.AppFragments.ProfileFragment;
import com.example.eco2.AppFragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;
    private String username;
    public static final String ACTION_PROFILE_UPDATED = "com.example.eco2.ACTION_PROFILE_UPDATED";
    private BroadcastReceiver profileUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_PROFILE_UPDATED.equals(intent.getAction())) {
                // Restart the MainActivity
                restartMainActivity();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set initial fragment when activity is created
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    selectorFragment = new HomeFragment();
                } else if (itemId == R.id.nav_search) {
                    selectorFragment = new SearchFragment();
                } else if (itemId == R.id.nav_upload) {
                    startActivity(new Intent(MainActivity.this, UploadActivity.class));
                    return false;
                } else if (itemId == R.id.nav_notifications) {
                    selectorFragment = new NotificationFragment();
                } else if (item.getItemId() == R.id.nav_profile) {
                    selectorFragment = new ProfileFragment();
                }
                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }

                return true;
            }
        });

        CircleImageView overlayProfileImage = findViewById(R.id.overlay_profile_image);

        loadProfileImage(overlayProfileImage);
        loadUsername();

        bottomNavigationView.setSelectedItemId(R.id.nav_home); // Set the home as the selected item

        // Register the receiver
        IntentFilter filter = new IntentFilter(ACTION_PROFILE_UPDATED);
        registerReceiver(profileUpdateReceiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the receiver
        unregisterReceiver(profileUpdateReceiver);
    }

    private void loadProfileImage(CircleImageView imageView) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String imageUrl = dataSnapshot.child("imageurl").getValue(String.class);
                        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("default")) {
                            Glide.with(MainActivity.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.profile_picture_place_holder)
                                    .into(imageView);
                        } else {
                            imageView.setImageResource(R.drawable.profile_picture_place_holder);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }
    }

    // Method to load username from Firebase
    private void loadUsername() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        username = dataSnapshot.child("username").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }
    }

    // Method to get the stored username
    public String getUsername() {
        return username;
    }
    public void restartMainActivity() {
        finish();
        startActivity(getIntent());
    }
}



