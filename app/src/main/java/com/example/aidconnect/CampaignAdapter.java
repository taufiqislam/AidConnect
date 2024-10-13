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
import java.util.concurrent.TimeUnit;

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

        // Correct way to calculate the days left between the deadline and creation date
        long diffInMillis = deadline.getTime() - new Date().getTime(); // Get difference in milliseconds from today
        long daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis); // Convert milliseconds to days

        // Set data to UI elements
        holder.campaignTitle.setText(campaign.getTitle());
        holder.campaignDeadline.setText("Deadline: " + daysLeft + " days left");
        holder.campaignDonors.setText("Donors: " + campaign.getDonorCount());
        holder.campaignImage.setImageResource(campaign.getImage());

        // Action button for donations
        holder.campaignActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, DonationActivity.class);
            intent.putExtra("campaignId", campaignId);
            intent.putExtra("campaignTitle", campaign.getTitle());
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
