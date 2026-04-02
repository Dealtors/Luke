package com.example.luke;

public class HistoryItem {
    private String address;
    private String[] items;
    private String date;

    public HistoryItem(String address, String[] items, String date) {
        this.address = address;
        this.items = items;
        this.date = date;
    }

    public String getAddress() { return address; }
    public String[] getItems() { return items; }
    public String getDate() { return date; }
}