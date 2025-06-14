package com.example.carrot.model;

import java.io.Serializable;

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

    public Product() {
        // 서버에서 파싱할 때 기본 생성자가 필요합니다.
    }

    // Getter (필수)
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public int getSeller_id() { return seller_id; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getLocation_name() { return location_name; }
    public String getImage() { return image; }
    public String getStatus() { return status; }
}
