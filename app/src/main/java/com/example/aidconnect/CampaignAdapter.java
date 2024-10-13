package com.example.aidconnect;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.CampaignViewHolder> {
    private List<Campaign> campaignList;
    private List<String> campaignIds;
    private Context context;

    public CampaignAdapter(List<Campaign> campaignList, List<String> campaignIds, Context context) {
        this.campaignList = campaignList;
        this.campaignIds = campaignIds;
        this.context = context;
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
        String campaignId = campaignIds.get(position);
        Date deadline = campaign.getCampaignDeadline();
        Date creationDate = campaign.getCampaignCreationDate();
        int daysLeft = deadline.getDay() - creationDate.getDay();
        holder.campaignTitle.setText(campaign.getTitle());
        holder.campaignDeadline.setText("Deadline: " + (daysLeft) + " days left");
        holder.campaignDonors.setText("Donors: " + campaign.getDonorCount());
        holder.campaignImage.setImageResource(campaign.getImage());  // Assuming image is a resource ID

        holder.campaignActionButton.setOnClickListener(v -> {
            // Create an Intent to navigate to DonationActivity
            Intent intent = new Intent(context, DonationActivity.class);

            // Pass relevant data to DonationActivity (like campaignId, title, etc.)
            intent.putExtra("campaignId", campaignId);
            intent.putExtra("campaignTitle", campaign.getTitle());

            // Start the DonationActivity
            context.startActivity(intent);
        });
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
