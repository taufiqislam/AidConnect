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

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MyCampaignAdapter extends RecyclerView.Adapter<MyCampaignAdapter.MyCampaignViewHolder> {
    private List<Campaign> campaignList;
    private List<String> campaignIds;
    private Context context;

    public MyCampaignAdapter(List<Campaign> campaignList, List<String> campaignIds, Context context) {
        this.campaignList = campaignList;
        this.campaignIds = campaignIds;
        this.context = context;
    }

    @NonNull
    @Override
    public MyCampaignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_campaign_item, parent, false);
        return new MyCampaignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyCampaignViewHolder holder, int position) {
        Campaign campaign = campaignList.get(position);
        String campaignId = campaignIds.get(position);
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MyCampaignDetailsActivity.class);
            intent.putExtra("campaign", campaign);
            intent.putExtra("campaignId", campaignId);
            context.startActivity(intent);
        });
    }

    private long getDaysLeft(Date deadline) {
        long diffInMillis = deadline.getTime() - new Date().getTime();
        return TimeUnit.MILLISECONDS.toDays(diffInMillis);
    }

    @Override
    public int getItemCount() {
        return campaignList.size();
    }

    public static class MyCampaignViewHolder extends RecyclerView.ViewHolder {
        TextView campaignTitle, campaignDeadline, campaignDonors;
        Button campaignActionButton;
        ImageView campaignImage;

        public MyCampaignViewHolder(@NonNull View itemView) {
            super(itemView);
            campaignTitle = itemView.findViewById(R.id.campaignTitle);
            campaignDeadline = itemView.findViewById(R.id.campaignDeadline);
            campaignDonors = itemView.findViewById(R.id.campaignDonors);
            campaignImage = itemView.findViewById(R.id.campaignImage);
            campaignActionButton = itemView.findViewById(R.id.campaignActionButton);
        }
    }
}
