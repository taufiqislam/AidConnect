package com.example.aidconnect;

import java.util.Date;

public class Campaign {
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

    // Constructor
    public Campaign(String title, String description, Date campaignCreationDate, Date campaignDeadline,
                    int donationTarget, int image, String category, String creatorId) {
        this.title = title;
        this.description = description;
        this.campaignCreationDate = campaignCreationDate;
        this.campaignDeadline = campaignDeadline;
        this.donorCount = 0;  // Initialized to 0 when a campaign is created
        this.donationTarget = donationTarget;
        this.currentDonation = 0;  // Starts with 0 donations
        this.image = image;
        this.category = category;
        this.creatorId = creatorId;
    }

    // Getters and Setters

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
