package com.example.aidconnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        this.db = FirebaseFirestore.getInstance();  // Initialize Firestore
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

        // Fetch campaign details using campaignId from Firestore
        fetchCampaignTitle(donation.getCampaignId(), holder.campaignTitle);

        // Set other donation data
        holder.donationAmount.setText("Amount: " + donation.getDonationAmount() + " BDT");
        holder.donationDate.setText("Date: " + donation.getDonationTime().toDate().toString());  // Convert Firestore Timestamp to Date
        holder.transactionId.setText("Transaction ID: " + donation.getTransactionId().toString());
    }

    // Helper method to fetch the campaign title from Firestore
    private void fetchCampaignTitle(String campaignId, TextView campaignTitleTextView) {
        db.collection("campaigns").document(campaignId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String campaignTitle = documentSnapshot.getString("title");
                        campaignTitleTextView.setText(campaignTitle);  // Set the campaign title in the TextView
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
}
