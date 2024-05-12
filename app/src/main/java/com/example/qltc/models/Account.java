package com.example.qltc.models;

public class Account {
    private long accountAmount;
    private String accountName;
    private int accountImage;

    public Account() {
    }

    public Account(long accountAmount, String accountName, int accountImage) {
        this.accountAmount = accountAmount;
        this.accountName = accountName;
        this.accountImage = accountImage;
    }

    public long getAccountAmount() {
        return accountAmount;
    }

    public void setAccountAmount(long accountAmount) {
        this.accountAmount = accountAmount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getAccountImage() {
        return accountImage;
    }

    public void setAccountImage(int accountImage) {
        this.accountImage = accountImage;
    }
}
