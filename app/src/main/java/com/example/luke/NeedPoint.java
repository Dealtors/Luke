package com.example.luke;

public class NeedPoint {
    private String pointName;
    private String city;
    private String address;
    private String timeUntil;
    private String[] items;
    private String contactName;
    private String contactPhone;
    private String need;
    private String urgency; // "СРОЧНО" или обычный
    private int iconResource; // для иконки

    public NeedPoint(String city, String address, String timeUntil, String[] items,
                     String contactName, String contactPhone, String need,
                     String urgency, int iconResource) {
        this(contactName, city, address, timeUntil, items, contactName, contactPhone, need, urgency, iconResource);
    }

    public NeedPoint(String pointName, String city, String address, String timeUntil, String[] items,
                     String contactName, String contactPhone, String need,
                     String urgency, int iconResource) {
        this.pointName = pointName;
        this.city = city;
        this.address = address;
        this.timeUntil = timeUntil;
        this.items = items;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.need = need;
        this.urgency = urgency;
        this.iconResource = iconResource;
    }

    public String getPointName() { return pointName != null ? pointName : contactName; }
    public String getCity() { return city; }
    public String getAddress() { return address; }
    public String getTimeUntil() { return timeUntil; }
    public String[] getItems() { return items; }
    public String getContactName() { return contactName; }
    public String getContactPhone() { return contactPhone; }
    public String getNeed() { return need; }
    public String getUrgency() { return urgency; }
    public int getIconResource() { return iconResource; }
}
