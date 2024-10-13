package com.example.aidconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

    private List<Donation> donationList;
    private Context context;

    public DonationAdapter(List<Donation> donationList, Context context) {
        this.donationList = donationList;
        this.context = context;
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_item, parent, false);
        return new DonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonationViewHolder holder, int position) {
        Donation donation = donationList.get(position);

        // Set data for each donation card
        holder.campaignTitle.setText(donation.getCampaignId());  // Fetch campaign title if necessary
        holder.donationAmount.setText("Amount: " + donation.getDonationAmount() + " BDT");
        holder.donationDate.setText("Date: " + donation.getDonationTime().toDate().toString());  // Convert Firestore Timestamp to Date
        holder.transactionId.setText("Transaction ID: " + donation.getTransactionId().toString());

    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView campaignTitle, donationAmount, donationDate,transactionId;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            campaignTitle = itemView.findViewById(R.id.tvCampaignTitle);
            donationAmount = itemView.findViewById(R.id.tvDonationAmount);
            donationDate = itemView.findViewById(R.id.tvDonationDate);
            transactionId = itemView.findViewById(R.id.tvTransactionId);
        }
    }
}
