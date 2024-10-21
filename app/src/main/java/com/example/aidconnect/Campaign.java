package com.example.aidconnect;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Campaign implements Serializable {
    private String title;
    private String description;
    private Date campaignCreationDate;
    private Date campaignDeadline;
    private int donorCount;
    private int donationTarget;
    private int currentDonation;
    private int image;  // Image resource or URL
    private String category;  // Newly Added, Popular, Urgency, Ending Soon
    private String creatorId;  // Foreign key linking to the creator (user)
    private Map<String, String> paymentMethods; // Map to hold payment method and number
    private String imageUrl;

    public Campaign() {
        // This constructor is needed by Firestore for deserialization
    }

    // Constructor with payment methods
    public Campaign(String title, String description, Date campaignCreationDate, Date campaignDeadline,
                    int donationTarget, int image, String category, String creatorId, Map<String, String> paymentMethods, String imageUrl) {
        this.title = title;
        this.description = description;
        this.campaignCreationDate = campaignCreationDate;
        this.campaignDeadline = campaignDeadline;
        this.donorCount = 0;
        this.donationTarget = donationTarget;
        this.currentDonation = 0;
        this.image = image;
        this.category = category;
        this.creatorId = creatorId;
        this.paymentMethods = paymentMethods;
        this.imageUrl = imageUrl;
    }

    // Getters and setters for all fields including payment methods
    public Map<String, String> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(Map<String, String> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    // Getters and Setters

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCampaignCreationDate() {
        return campaignCreationDate;
    }

    public void setCampaignCreationDate(Date campaignCreationDate) {
        this.campaignCreationDate = campaignCreationDate;
    }

    public Date getCampaignDeadline() {
        return campaignDeadline;
    }

    public void setCampaignDeadline(Date campaignDeadline) {
        this.campaignDeadline = campaignDeadline;
    }

    public int getDonorCount() {
        return donorCount;
    }

    public void setDonorCount(int donorCount) {
        this.donorCount = donorCount;
    }

    public int getDonationTarget() {
        return donationTarget;
    }

    public void setDonationTarget(int donationTarget) {
        this.donationTarget = donationTarget;
    }

    public int getCurrentDonation() {
        return currentDonation;
    }

    public void setCurrentDonation(int currentDonation) {
        this.currentDonation = currentDonation;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}
