package com.example.aidconnect;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button signUpButton;
    private TextView loginTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        loginTextView = findViewById(R.id.loginTextView);

        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(v -> signUpUser());

        loginTextView.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }

    private void signUpUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Redirect to login or main activity
                Toast.makeText(SignupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            } else {
                Toast.makeText(SignupActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
