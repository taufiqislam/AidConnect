package com.example.aidconnect;

import com.google.firebase.Timestamp;

public class Donation {
    private String donorId;
    private String campaignId;
    private int donationAmount;
    private Timestamp donationTime;
    private String transactionId;

    public Donation(String userId, String campaignId, int donationAmount, Timestamp now, String transactionId, String selectedMedium) {}

    public Donation() {
    }

    public Donation(String donorId, String campaignId, int donationAmount, Timestamp donationTime, String transactionId)
    {
        this.donorId = donorId;
        this.campaignId = campaignId;
        this.donationAmount = donationAmount;
        this.donationTime = donationTime;
        this.transactionId = transactionId;
    }

    public String getDonorId() {
        return donorId;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public int getDonationAmount() {
        return donationAmount;
    }

    public void setDonationAmount(int donationAmount) {
        this.donationAmount = donationAmount;
    }

    public void setDonorId(String donorId) {
        this.donorId = donorId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Timestamp getDonationTime() {
        return donationTime;
    }

    public void setDonationTime(Timestamp donationTime) {
        this.donationTime = donationTime;
    }
}
