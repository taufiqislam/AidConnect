package com.example.aidconnect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DonationActivity extends BaseActivity {

    private EditText inputDonationAmount;
    private Button btnDonate;
    private ProgressBar progressBar;
    private TextView campaignTitleTV;
    private String campaignId;
    private String campaignTitle;

    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            setupDrawer();
        }

        inputDonationAmount = findViewById(R.id.inputDonationAmount);
        btnDonate = findViewById(R.id.btnDonate);
        progressBar = findViewById(R.id.donationProgressBar);
        campaignTitleTV = findViewById(R.id.campaignTitle);

        Intent intent = getIntent();
        campaignId = intent.getStringExtra("campaignId");
        campaignTitle = intent.getStringExtra("campaignTitle");

        campaignTitleTV.setText(campaignTitle);
        // Get the current user ID (donor)
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get the campaignId passed from the previous activity
        campaignId = getIntent().getStringExtra("campaignId");

        btnDonate.setOnClickListener(v -> {
            String donationAmountStr = inputDonationAmount.getText().toString();

            if (donationAmountStr.isEmpty()) {
                inputDonationAmount.setError("Please enter an amount");
                return;
            }

            int donationAmount = Integer.parseInt(donationAmountStr);

            // Process the donation
            processDonation(donationAmount);
        });
    }

    private void processDonation(int donationAmount) {
        progressBar.setVisibility(View.VISIBLE);

        // You would typically call SSLCOMMERZ here to process the payment
        // For now, let's assume the payment is successful and proceed to save the donation data

        // Generate a dummy transaction ID
        String transactionId = "txn_" + System.currentTimeMillis();

        // Save donation details to Firestore
        saveDonationToFirestore(donationAmount, transactionId);
    }

    private void saveDonationToFirestore(int donationAmount, String transactionId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new donation object
        Donation donation = new Donation(userId, campaignId, donationAmount, Timestamp.now(), transactionId);

        // Save donation data in Firestore under a "donations" collection
        db.collection("donations")
                .add(donation)
                .addOnSuccessListener(documentReference -> {
                    // Update the campaign details
                    updateCampaignAfterDonation(donationAmount);
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("DonationActivity", "Error saving donation", e);
                    Toast.makeText(DonationActivity.this, "Donation failed!", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCampaignAfterDonation(int donationAmount) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        progressBar.setVisibility(View.VISIBLE);

        // Check if the user has already donated to this campaign
        db.collection("donations")
                .whereEqualTo("donorId", userId)
                .whereEqualTo("campaignId", campaignId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean hasDonatedBefore = queryDocumentSnapshots.size() > 1;

                    // Reference to the campaign document
                    db.collection("campaigns").document(campaignId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    // Retrieve current values
                                    int currentDonation = documentSnapshot.getLong("currentDonation").intValue();
                                    int donorCount = documentSnapshot.getLong("donorCount").intValue();

                                    // Prepare updates
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("currentDonation", currentDonation + donationAmount);

                                    // If the user hasn't donated before, increment donorCount
                                    if (!hasDonatedBefore) {
                                        updates.put("donorCount", donorCount + 1);
                                    }

                                    // Update the campaign document with new donation data
                                    db.collection("campaigns").document(campaignId)
                                            .update(updates)
                                            .addOnSuccessListener(aVoid -> {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(DonationActivity.this, "Donation successful!", Toast.LENGTH_SHORT).show();

                                                // Redirect back to campaign page or show confirmation
                                                Intent intent = new Intent(DonationActivity.this, CampaignActivity.class);
                                                startActivity(intent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                progressBar.setVisibility(View.GONE);
                                                Log.e("DonationActivity", "Error updating campaign", e);
                                                Toast.makeText(DonationActivity.this, "Failed to update campaign!", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Log.e("DonationActivity", "Error retrieving campaign", e);
                                Toast.makeText(DonationActivity.this, "Error retrieving campaign details!", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("DonationActivity", "Error checking previous donations", e);
                    Toast.makeText(DonationActivity.this, "Error verifying previous donations!", Toast.LENGTH_SHORT).show();
                });
    }



}
