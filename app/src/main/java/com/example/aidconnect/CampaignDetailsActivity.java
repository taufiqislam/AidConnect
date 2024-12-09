package com.example.aidconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CampaignDetailsActivity extends BaseActivity {

    private ImageView campaignImageView;
    private TextView campaignTitle, campaignDescription, campaignCreator, tvCurrentDonation, tvDonorCount, tvDaysLeft;
    private ProgressBar donationProgressBar;
    private Button donateButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_details);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            setupDrawer();
        }

        Intent intent = getIntent();
        String campaignId = intent.getStringExtra("campaignId");

        initializeViews();

        fetchCampaignDetails(campaignId);
    }

    private void initializeViews() {
        campaignImageView = findViewById(R.id.campaignImageView);
        campaignTitle = findViewById(R.id.campaignTitle);
        campaignDescription = findViewById(R.id.campaignDescription);
        campaignCreator = findViewById(R.id.campaignCreator);
        donateButton = findViewById(R.id.btnDonate);
        donationProgressBar = findViewById(R.id.donationProgressBar);
        tvCurrentDonation = findViewById(R.id.tvCurrentDonation);
        tvDonorCount = findViewById(R.id.tvDonorCount);
        tvDaysLeft = findViewById(R.id.tvDaysLeft);
    }

    private void fetchCampaignDetails(String campaignId) {
        if (campaignId == null || campaignId.isEmpty()) {
            Toast.makeText(this, "Invalid Campaign ID", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("campaigns").document(campaignId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Campaign campaign = documentSnapshot.toObject(Campaign.class);
                        if (campaign != null) {
                            updateUI(campaign, campaignId);
                        }
                    } else {
                        Toast.makeText(this, "Campaign not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching campaign details", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUI(Campaign campaign, String campaignId) {
        Glide.with(this)
                .load(campaign.getImageUrl())
                .placeholder(R.drawable.sample)
                .error(R.drawable.sample)
                .into(campaignImageView);

        campaignTitle.setText(campaign.getTitle());
        campaignDescription.setText(campaign.getDescription());

        fetchCreatorName(campaign.getCreatorId());

        // Calculate and display progress
        int progress = (campaign.getCurrentDonation() * 100) / campaign.getDonationTarget();
        donationProgressBar.setProgress(progress);

        tvCurrentDonation.setText("Donation: TK" + campaign.getCurrentDonation() + "\nDonation Goal: TK" + campaign.getDonationTarget());
        tvDonorCount.setText(campaign.getDonorCount() + "\nDonors");
        long daysLeft = getDaysLeft(campaign.getCampaignDeadline());
        tvDaysLeft.setText(daysLeft >= 0 ? daysLeft + "\nDays Left" : "Campaign\nEnded");

        // Handle the donate button
        donateButton.setOnClickListener(v -> {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                Intent donationIntent = new Intent(CampaignDetailsActivity.this, DonationActivity.class);
                donationIntent.putExtra("campaignId", campaignId);
                donationIntent.putExtra("campaignTitle", campaign.getTitle());
                startActivity(donationIntent);
            } else {
                Intent loginIntent = new Intent(CampaignDetailsActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    private long getDaysLeft(Date deadline) {
        long diffInMillis = deadline.getTime() - new Date().getTime();
        return TimeUnit.MILLISECONDS.toDays(diffInMillis);
    }

    private void fetchCreatorName(String creatorId) {
        db.collection("users").document(creatorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        campaignCreator.setText("Created by: " + firstName + " " + lastName);
                    } else {
                        campaignCreator.setText("Creator not found");
                    }
                })
                .addOnFailureListener(e -> {
                    campaignCreator.setText("Failed to load creator info");
                });
    }
}
