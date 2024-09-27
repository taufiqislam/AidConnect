package com.example.aidconnect;

public class Payment {
    private String campaignId;
    private String paymentCategory;
    private String accountNumber;

    public Payment(String campaignId, String paymentCategory, String accountNumber) {
        this.campaignId = campaignId;
        this.paymentCategory = paymentCategory;
        this.accountNumber = accountNumber;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPaymentCategory() {
        return paymentCategory;
    }

    public void setPaymentCategory(String paymentCategory) {
        this.paymentCategory = paymentCategory;
    }
}
