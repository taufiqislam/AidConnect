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

        // Firebase initialization, drawer setup, and other views initialization
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        if (currentUser != null) {
            setupDrawer();
        }

        // Get the campaign details from the intent
        Intent intent = getIntent();
        Campaign campaign = (Campaign) intent.getSerializableExtra("campaign");
        String id = intent.getStringExtra("campaignId");
        String title = campaign.getTitle();
        String description = campaign.getDescription();
        String creatorId = campaign.getCreatorId();
        int imageResId = campaign.getImage();
        int donationTarget = campaign.getDonationTarget();
        int currentDonation = campaign.getCurrentDonation();
        int donorCount = campaign.getDonorCount();
        Date deadline = campaign.getCampaignDeadline();
        Date creationDate = campaign.getCampaignCreationDate();

        // Find views by ID
        campaignImageView = findViewById(R.id.campaignImageView);
        campaignTitle = findViewById(R.id.campaignTitle);
        campaignDescription = findViewById(R.id.campaignDescription);
        campaignCreator = findViewById(R.id.campaignCreator);
        donateButton = findViewById(R.id.btnDonate);
        donationProgressBar = findViewById(R.id.donationProgressBar);
        tvCurrentDonation = findViewById(R.id.tvCurrentDonation);
        tvDonorCount = findViewById(R.id.tvDonorCount);
        tvDaysLeft = findViewById(R.id.tvDaysLeft);

        // Set the campaign details in the views
        Glide.with(this)
                .load(campaign.getImageUrl()) // Assuming this is a URL, change if it's a resource ID
                .placeholder(R.drawable.sample) // A placeholder image while loading
                .error(R.drawable.sample) // An error image in case loading fails
                .into(campaignImageView);
        //campaignImageView.setImageResource(imageResId);
        campaignTitle.setText(title);
        campaignDescription.setText(description);

        // Calculate and set the progress bar
        int progress = (currentDonation * 100) / donationTarget;
        donationProgressBar.setProgress(progress);

        // Set the statistics
        tvCurrentDonation.setText("Donation: TK" + currentDonation + "\nDonation Goal: TK" + donationTarget);
        tvDonorCount.setText(donorCount + "\nDonors");
        tvDaysLeft.setText(getDaysLeft(deadline) + "\nDays Left");

        // Fetch and set the creator's name
        fetchCreatorName(creatorId);

        // Set up the donate button click listener
        donateButton.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() != null) {
                Intent donationIntent = new Intent(CampaignDetailsActivity.this, DonationActivity.class);
                donationIntent.putExtra("campaignId", id);
                donationIntent.putExtra("campaignTitle", title);
                startActivity(donationIntent);
            } else {
                // User is not logged in, redirect to LoginActivity
                Intent loginIntent = new Intent(CampaignDetailsActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }

        });
    }

    // Helper method to calculate days left until the deadline
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

}
