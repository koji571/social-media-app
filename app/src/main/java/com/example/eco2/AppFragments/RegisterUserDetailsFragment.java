package com.example.eco2.AppFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eco2.R;
import com.example.eco2.RegisterActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterUserDetailsFragment extends Fragment {

    private EditText username;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button register;
    private TextView loginUser;

    public RegisterUserDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_user_details, container, false);

        // Initialize your views
        username = view.findViewById(R.id.username);
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        register = view.findViewById(R.id.register);
        loginUser = view.findViewById(R.id.login_user);

        Button button = view.findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


        return view;
    }

    private void registerUser() {
        String txtUsername = username.getText().toString().trim();
        String txtName = name.getText().toString().trim();
        String txtEmail = email.getText().toString().trim();
        String txtPassword = password.getText().toString().trim();

        if (txtUsername.isEmpty() || txtName.isEmpty() || txtEmail.isEmpty() || txtPassword.isEmpty()) {
            Toast.makeText(getActivity(), "Certain details are not filled!", Toast.LENGTH_SHORT).show();
        } else if (txtPassword.length() < 6) {
            Toast.makeText(getActivity(), "Your password is too short! (Minimum 6 characters)", Toast.LENGTH_SHORT).show();
        } else {
            // Store user details in RegisterActivity
            ((RegisterActivity) getActivity()).storeUserDetails(txtUsername, txtName, txtEmail, txtPassword);
            // Navigate to the next fragment
            ((RegisterActivity) getActivity()).toProfilePictureFragment();
        }
    }
}
