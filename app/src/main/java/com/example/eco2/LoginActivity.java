package com.example.eco2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private TextView registerUser;

    private FirebaseAuth mAuth;

    // Views for the login page
    private ImageView iconImage;
    private ConstraintLayout loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to the login page layout
        setContentView(R.layout.login_page);

        // Initialize the views
        iconImage = findViewById(R.id.logoImage);
        loginLayout = findViewById(R.id.loginLayout);

        // Initially hide the login layout with an alpha animation
        loginLayout.animate().alpha(0f).setDuration(10);

        // Create a translate animation for the icon image
        TranslateAnimation animation = new TranslateAnimation(0, 0, 1000, 0);
        animation.setDuration(1200); // Set duration of the animation
        animation.setFillAfter(false); // Do not keep the end state after animation
        animation.setAnimationListener(new MyAnimationListener()); // Set the animation listener

        // Start the animation on the icon image
        iconImage.setAnimation(animation);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginButton);

        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        if (mAuth.getCurrentUser() != null) {
            // User is already logged in, redirect to MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return; // This is important to stop further execution
        }

    }

    // Custom animation listener class
    private class MyAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
            // Actions to perform when the animation starts
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            // Actions to perform when the animation ends

            // Clear the animation on the icon image
            iconImage.clearAnimation();
            // Make the icon image visible
            iconImage.setVisibility(View.VISIBLE);
            // Animate the login layout to become visible
            loginLayout.animate().alpha(1f).setDuration(500);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // Actions to perform on animation repeat
        }
    }

    // Method to handle the login button click
    public void onLoginButtonClick(View view) {
        String txt_email = email.getText().toString();
        String txt_password = password.getText().toString();

        if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
            Toast.makeText(LoginActivity.this, "Certain details are not filled!", Toast.LENGTH_SHORT).show();
        } else {
            loginUser(txt_email , txt_password);
        }
    }

    private void loginUser(String txt_email, String txt_password) {
        mAuth.signInWithEmailAndPassword(txt_email , txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {;
                    Intent intent = new Intent(LoginActivity.this , MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onRegisterClick(View view) {
        // Create an intent to start the FeedActivity
        Intent intent = new Intent(this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Start the RegisterActivity
        startActivity(intent);
    }

    public void onForgetPasswordClick(View view) {
        // Create an intent to start the FeedActivity
        Intent intent = new Intent(this, ForgetPasswordActivity.class);
        // Start the ForgetPasswordActivity
        startActivity(intent);
    }
}
