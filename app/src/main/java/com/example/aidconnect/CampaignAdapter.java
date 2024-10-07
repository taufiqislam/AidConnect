package com.example.aidconnect;

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

    public CampaignAdapter(List<Campaign> campaignList) {
        this.campaignList = campaignList;
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
        Date creationDate = campaign.getCampaignCreationDate();
        int daysLeft = deadline.getDay() - creationDate.getDay();
        holder.campaignTitle.setText(campaign.getTitle());
        holder.campaignDeadline.setText("Deadline: " + (daysLeft) + " days left");
        holder.campaignDonors.setText("Donors: " + campaign.getDonorCount());
        holder.campaignImage.setImageResource(campaign.getImage());  // Assuming image is a resource ID
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
        }
    }
}

