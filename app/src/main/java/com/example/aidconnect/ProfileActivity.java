package com.example.aidconnect;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView firstNameTextView, lastNameTextView, emailTextView, phoneTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI components
        firstNameTextView = findViewById(R.id.textFirstName);
        lastNameTextView = findViewById(R.id.textLastName);
        emailTextView = findViewById(R.id.textEmail);
        phoneTextView = findViewById(R.id.textPhone);

        // Get user details from Firestore
        getUserDetails();
    }

    private void getUserDetails() {
        String currentUserId = auth.getCurrentUser().getUid();  // Get the current user's ID

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Map Firestore data to User object
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("phoneNumber");

                        // Set user details to UI elements
                        firstNameTextView.setText(firstName);
                        lastNameTextView.setText(lastName);
                        emailTextView.setText(email);
                        phoneTextView.setText(phone);
                    } else {
                        Log.d("ProfileActivity", "No such user data found.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("ProfileActivity", "Error getting user details", e);
                });
    }
}
