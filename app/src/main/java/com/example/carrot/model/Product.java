package com.example.carrot.model;

import java.io.Serializable;

// Product.java

public class Product implements Serializable {
    private int id;
    private String title;
    private String description;
    private int price;
    private int seller_id;
    private Double latitude;
    private Double longitude;
    private String location_name;
    private String image;
    private String status;
    private String seller_nickname;

    public Product() {}

    public Product(String title, String description, int price, int seller_id,
                   Double latitude, Double longitude, String location_name, String image) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.seller_id = seller_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location_name = location_name;
        this.image = image;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public int getSeller_id() { return seller_id; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getLocation_name() { return location_name; }
    public String getImage() { return image; }
    public String getStatus() { return status != null ? status : "판매중"; }

    public String getSeller_nickname() {  // 🔹 getter 추가
        return seller_nickname;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSeller_nickname(String seller_nickname) {  // 🔹 setter도 있으면 좋아요
        this.seller_nickname = seller_nickname;
    }
}
