package com.example.luke;

import java.util.ArrayList;
import java.util.List;

public class PointProfile {
    private String pointName;
    private String city;
    private String address;
    private double latitude;
    private double longitude;
    private String contactName;
    private String contactPhone;
    private String email;
    private String password;
    private String workHours;
    private final List<Product> products = new ArrayList<>();

    public PointProfile(String pointName, String city, String address,
                        String contactName, String contactPhone, String email,
                        String password, String workHours) {
        this(pointName, city, address, 0, 0, contactName, contactPhone, email, password, workHours);
    }

    public PointProfile(String pointName, String city, String address,
                        double latitude, double longitude,
                        String contactName, String contactPhone, String email,
                        String password, String workHours) {
        this.pointName = pointName;
        this.city = city;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.email = email;
        this.password = password;
        this.workHours = workHours;
    }

    public String getPointName() { return pointName; }
    public void setPointName(String pointName) { this.pointName = pointName; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getWorkHours() { return workHours; }
    public void setWorkHours(String workHours) { this.workHours = workHours; }

    public List<Product> getProducts() { return products; }
}
