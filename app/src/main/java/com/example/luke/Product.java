package com.example.luke;

public class Product {
    private long id;
    private String pointAddress;
    private String name;
    private String description;
    private int totalQuantity;
    private int collectedQuantity;
    private String urgency;

    public Product(String name, String description, int quantity) {
        this(name, description, quantity, 0, "Обычная");
    }

    public Product(String name, String description, int totalQuantity, int collectedQuantity, String urgency) {
        this(0, null, name, description, totalQuantity, collectedQuantity, urgency);
    }

    public Product(long id, String pointAddress, String name, String description, int totalQuantity, int collectedQuantity, String urgency) {
        this.id = id;
        this.pointAddress = pointAddress;
        this.name = name;
        this.description = description;
        this.totalQuantity = totalQuantity;
        this.collectedQuantity = collectedQuantity;
        this.urgency = urgency;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getPointAddress() { return pointAddress; }
    public void setPointAddress(String pointAddress) { this.pointAddress = pointAddress; }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getTotalQuantity() { return totalQuantity; }
    public int getCollectedQuantity() { return collectedQuantity; }
    public String getUrgency() { return urgency; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
    public void setCollectedQuantity(int collectedQuantity) { this.collectedQuantity = collectedQuantity; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public int getProgress() {
        if (totalQuantity <= 0) return 0;
        return Math.min(100, (int) ((float) collectedQuantity / totalQuantity * 100));
    }
}
