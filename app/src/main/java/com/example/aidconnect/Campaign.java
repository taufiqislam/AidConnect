package com.example.aidconnect;

public class Campaign {
    private String title;
    private int daysLeft;
    private int donorCount;
    private int image;
    private String actionButtonText;
    private String category; // Newly Added, Popular, Urgency, Ending Soon

    public Campaign(String title, int daysLeft, int donorCount, int image, String actionButtonText, String category) {
        this.title = title;
        this.daysLeft = daysLeft;
        this.donorCount = donorCount;
        this.image = image;
        this.actionButtonText = actionButtonText;
        this.category = category;
    }

    public String getTitle() { return title; }
    public int getDaysLeft() { return daysLeft; }
    public int getDonorCount() { return donorCount; }
    public int getImage() { return image; }
    public String getActionButtonText() { return actionButtonText; }
    public String getCategory() { return category; }
}

