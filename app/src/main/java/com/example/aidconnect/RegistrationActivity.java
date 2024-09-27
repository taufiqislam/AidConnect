package com.example.aidconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, emailInput, passwordInput, phoneInput, ageInput, genderInput;
    private Button registerButton;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        firstNameInput = findViewById(R.id.inputFirstName);
        lastNameInput = findViewById(R.id.inputLastName);
        emailInput = findViewById(R.id.inputEmail);
        passwordInput = findViewById(R.id.inputPassword);
        phoneInput = findViewById(R.id.inputPhone);
        registerButton = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Set button click listener
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        // Get user input
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() ||
                phone.isEmpty() ) {
            Toast.makeText(RegistrationActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        // Register user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();

                            // Create user object
                            User user = new User(firstName, lastName, email, phone);

                            // Store user info in Firestore
                            db.collection("users")
                                    .document(userId)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "User added to Firestore");
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(RegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                        // Redirect to CampaignActivity
                                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error adding user to Firestore", e);
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(RegistrationActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Handle registration failure
                        Log.e("FirebaseAuth", "Registration failed", task.getException());
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(RegistrationActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
