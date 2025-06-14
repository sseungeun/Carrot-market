package com.example.carrot.model;

public class ProductRequest {
    private String title;
    private String description;
    private int price;
    private int seller_id;
    private Double latitude;
    private Double longitude;
    private String location_name;
    private String image;

    public ProductRequest(String title, String description, int price, int seller_id,
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
}

