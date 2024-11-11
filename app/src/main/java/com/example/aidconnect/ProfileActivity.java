package com.example.aidconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends BaseActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private TextView firstNameTextView, lastNameTextView, emailTextView, phoneTextView;

    private Button campaignButton,donationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            setupDrawer();
        }

        // Initialize UI components
        firstNameTextView = findViewById(R.id.textFirstName);
        lastNameTextView = findViewById(R.id.textLastName);
        emailTextView = findViewById(R.id.textEmail);
        phoneTextView = findViewById(R.id.textPhone);
        campaignButton=findViewById(R.id.btnMyCampaigns);
        donationButton=findViewById(R.id.btnMyDonations);

        campaignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,MyCampaignsActivity.class);
                startActivity(intent);
            }
        });

        donationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,MyDonationsActivity.class);
                startActivity(intent);
            }
        });

        getUserDetails();
    }

    private void getUserDetails() {
        String currentUserId = auth.getCurrentUser().getUid();  // Get the current user's ID

        db.collection("users").document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String email = documentSnapshot.getString("email");
                        String phone = documentSnapshot.getString("phoneNumber");

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
