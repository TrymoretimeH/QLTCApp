package com.example.qltc.models;

import java.io.Serializable;
import java.util.Date;

//import io.realm.RealmObject;
//import io.realm.annotations.PrimaryKey;


public class Transaction implements Serializable {
    private String type, category = "Default", account = "Default", note;
    private Date date;
    private int amount;

//    @PrimaryKey
    private String id;

    public Transaction() {
    }

    public Transaction(String type, String category, String account, String note, Date date, int amount, String id) {
        this.type = type;
        this.category = category;
        this.account = account;
        this.note = note;
        this.date = date;
        this.amount = amount;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
