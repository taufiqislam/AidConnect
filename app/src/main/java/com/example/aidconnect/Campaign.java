package com.example.aidconnect;

public class Campaign {
    private String title;
    private String Description;
    private int daysLeft;
    private int donorCount;
    private int donationTarget;
    private int currentDonation;
    private int image;
    private String category; // Newly Added, Popular, Urgency, Ending Soon

    public Campaign(String title, int daysLeft, int donorCount, int image, String category) {
        this.title = title;
        this.daysLeft = daysLeft;
        this.donorCount = donorCount;
        this.image = image;
        this.category = category;
    }

    public String getTitle() { return title; }
    public int getDaysLeft() { return daysLeft; }
    public int getDonorCount() { return donorCount; }
    public int getImage() { return image; }
    public String getCategory() { return category; }
}

