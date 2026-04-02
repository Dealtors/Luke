package com.example.luke;

public class PointItem {
    private String name;
    private int needed;
    private int collected;

    public PointItem(String name, int needed, int collected) {
        this.name = name;
        this.needed = needed;
        this.collected = collected;
    }

    public String getName() { return name; }
    public int getNeeded() { return needed; }
    public int getCollected() { return collected; }
    public int getProgress() {
        return (int) ((float) collected / needed * 100);
    }
}