package com.example.aidconnect;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyCampaignDetailsActivity extends BaseActivity {

    private ImageView campaignImageView;
    private TextView campaignTitle, campaignDescription, campaignCreator, tvCurrentDonation, tvDonorCount, tvDaysLeft;
    private ProgressBar donationProgressBar;
    private Button editButton;
    private FirebaseFirestore db;
    private ListenerRegistration campaignListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_campaign_details);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if (currentUser != null) {
            setupDrawer();
        }

        // Get the campaignId from the intent
        Intent intent = getIntent();
        String campaignId = intent.getStringExtra("campaignId");

        // Initialize UI elements
        campaignImageView = findViewById(R.id.campaignImageView);
        campaignTitle = findViewById(R.id.campaignTitle);
        campaignDescription = findViewById(R.id.campaignDescription);
        campaignCreator = findViewById(R.id.campaignCreator);
        editButton = findViewById(R.id.btnEditCampaign);
        donationProgressBar = findViewById(R.id.donationProgressBar);
        tvCurrentDonation = findViewById(R.id.tvCurrentDonation);
        tvDonorCount = findViewById(R.id.tvDonorCount);
        tvDaysLeft = findViewById(R.id.tvDaysLeft);

        // Fetch campaign details and listen for updates
        fetchCampaignDetails(campaignId);

        editButton.setOnClickListener(v -> {
            Intent updateIntent = new Intent(MyCampaignDetailsActivity.this, UpdateCampaignActivity.class);
            updateIntent.putExtra("campaignId", campaignId);
            startActivity(updateIntent);
        });
    }

    private void fetchCampaignDetails(String campaignId) {
        DocumentReference campaignRef = db.collection("campaigns").document(campaignId);

        // Set up snapshot listener to listen for real-time updates
        campaignListener = campaignRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                campaignTitle.setText("Failed to load campaign details");
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Campaign campaign = documentSnapshot.toObject(Campaign.class);
                if (campaign != null) {
                    updateUI(campaign);
                }
            }
        });
    }

    private void updateUI(Campaign campaign) {
        // Set image, title, description, and creator's name
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
        tvDaysLeft.setText(getDaysLeft(campaign.getCampaignDeadline()) + "\nDays Left");
    }

    private long getDaysLeft(Date deadline) {
        long diffInMillis = deadline.getTime() - new Date().getTime();
        return TimeUnit.MILLISECONDS.toDays(diffInMillis);
    }

    private void fetchCreatorName(String creatorId) {
        db.collection("users").document(creatorId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String firstName = documentSnapshot.getString("firstName");
                String lastName = documentSnapshot.getString("lastName");
                campaignCreator.setText("Created by: " + firstName + " " + lastName);
            } else {
                campaignCreator.setText("Creator not found");
            }
        }).addOnFailureListener(e -> {
            campaignCreator.setText("Failed to load creator info");
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Detach listener to avoid memory leaks
        if (campaignListener != null) {
            campaignListener.remove();
        }
    }
}
