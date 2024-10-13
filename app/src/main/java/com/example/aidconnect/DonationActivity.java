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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DonationActivity extends AppCompatActivity {

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
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(DonationActivity.this, "Donation successful!", Toast.LENGTH_SHORT).show();

                    // Redirect back to campaign page or show confirmation
                    Intent intent = new Intent(DonationActivity.this, CampaignActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Log.e("DonationActivity", "Error saving donation", e);
                    Toast.makeText(DonationActivity.this, "Donation failed!", Toast.LENGTH_SHORT).show();
                });
    }
}
