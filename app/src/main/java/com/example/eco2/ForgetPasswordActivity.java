package com.example.eco2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth = FirebaseAuth.getInstance();
    }

    public void onForgetClick(View view) {
        EditText emailField = findViewById(R.id.email);
        String email = emailField.getText().toString().trim();

        if (email.isEmpty()) {
            emailField.setError("Email is required");
            emailField.requestFocus();
            return;
        }

        // Send password reset email
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgetPasswordActivity.this, "Password reset email sent", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, "Failed to send reset email", Toast.LENGTH_LONG).show();
                    }
                });
    }
    public void onLogin(View view) {
        finish();
    }
}