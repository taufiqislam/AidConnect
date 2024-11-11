package com.example.aidconnect;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.CampaignViewHolder> {
    private List<Campaign> campaignList;
    private Context context;
    private FirebaseFirestore db;

    public CampaignAdapter(List<Campaign> campaignList, Context context) {
        this.campaignList = campaignList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    @NonNull
    @Override
    public CampaignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.campaign_item, parent, false);
        return new CampaignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampaignViewHolder holder, int position) {
        Campaign campaign = campaignList.get(position);
        Date deadline = campaign.getCampaignDeadline();
        long daysLeft = getDaysLeft(deadline);

        holder.campaignTitle.setText(campaign.getTitle());
        holder.campaignDeadline.setText("Deadline: " + daysLeft + " days left");
        holder.campaignDonors.setText("Donors: " + campaign.getDonorCount());

        Glide.with(context)
                .load(campaign.getImageUrl())
                .placeholder(R.drawable.sample)
                .error(R.drawable.sample)
                .into(holder.campaignImage);

        // Set up action button
        holder.campaignActionButton.setOnClickListener(v -> handleActionButtonClick(campaign));

        // Set up item click for detailed view
        holder.itemView.setOnClickListener(v -> openCampaignDetails(campaign));
    }

    private long getDaysLeft(Date deadline) {
        long diffInMillis = deadline.getTime() - new Date().getTime();
        return TimeUnit.MILLISECONDS.toDays(diffInMillis);
    }

    private void handleActionButtonClick(Campaign campaign) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // Fetch campaign ID based on title for DonationActivity
            db.collection("campaigns")
                    .whereEqualTo("title", campaign.getTitle())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            String campaignId = queryDocumentSnapshots.getDocuments().get(0).getId();
                            Intent intent = new Intent(context, DonationActivity.class);
                            intent.putExtra("campaignId", campaignId);
                            intent.putExtra("campaignTitle", campaign.getTitle());
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Campaign not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Error fetching campaign ID", Toast.LENGTH_SHORT).show());
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }
    }

    private void openCampaignDetails(Campaign campaign) {
        // Fetch campaign ID based on title for CampaignDetailsActivity
        db.collection("campaigns")
                .whereEqualTo("title", campaign.getTitle())
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        String campaignId = queryDocumentSnapshots.getDocuments().get(0).getId();
                        Intent intent = new Intent(context, CampaignDetailsActivity.class);
                        intent.putExtra("campaign", campaign);
                        intent.putExtra("campaignId", campaignId);
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Campaign not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error fetching campaign ID", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return campaignList.size();
    }

    public static class CampaignViewHolder extends RecyclerView.ViewHolder {
        TextView campaignTitle, campaignDeadline, campaignDonors;
        Button campaignActionButton;
        ImageView campaignImage;

        public CampaignViewHolder(@NonNull View itemView) {
            super(itemView);
            campaignTitle = itemView.findViewById(R.id.campaignTitle);
            campaignDeadline = itemView.findViewById(R.id.campaignDeadline);
            campaignDonors = itemView.findViewById(R.id.campaignDonors);
            campaignImage = itemView.findViewById(R.id.campaignImage);
            campaignActionButton = itemView.findViewById(R.id.campaignActionButton);
        }
    }
}
