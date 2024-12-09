package com.example.aidconnect;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.DonationViewHolder> {

    private List<Donation> donationList;
    private Context context;
    private FirebaseFirestore db;

    public DonationAdapter(List<Donation> donationList, Context context) {
        this.donationList = donationList;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
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

        fetchCampaignTitle(donation.getCampaignId(), holder.campaignTitle);

        // Set other donation data
        holder.donationAmount.setText("Amount: " + donation.getDonationAmount() + " BDT");
        holder.donationDate.setText("Date: " + donation.getDonationTime().toDate().toString());
        holder.transactionId.setText("Transaction ID: " + donation.getTransactionId().toString());

        holder.itemView.setOnClickListener(v -> openCampaignDetails(donation.getCampaignId()));
    }

    private void fetchCampaignTitle(String campaignId, TextView campaignTitleTextView) {
        db.collection("campaigns").document(campaignId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String campaignTitle = documentSnapshot.getString("title");
                        campaignTitleTextView.setText(campaignTitle);
                    } else {
                        campaignTitleTextView.setText("Campaign not found");
                    }
                })
                .addOnFailureListener(e -> {
                    campaignTitleTextView.setText("Failed to load campaign");
                });
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public static class DonationViewHolder extends RecyclerView.ViewHolder {
        TextView campaignTitle, donationAmount, donationDate, transactionId;

        public DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            campaignTitle = itemView.findViewById(R.id.tvCampaignTitle);
            donationAmount = itemView.findViewById(R.id.tvDonationAmount);
            donationDate = itemView.findViewById(R.id.tvDonationDate);
            transactionId = itemView.findViewById(R.id.tvTransactionId);
        }
    }

    private void openCampaignDetails(String campaignId) {
        Intent intent = new Intent(context, CampaignDetailsActivity.class);
        intent.putExtra("campaignId", campaignId);
        context.startActivity(intent);
    }
}
