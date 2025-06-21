package com.example.carrot.model;

public class Message {
    private int product_id;
    private int sender_id;
    private int receiver_id;
    private String content;
    private double latitude;
    private double longitude;
    private String location_name;
    private long timestamp; // 추가

    public Message(int product_id, int sender_id, int receiver_id, String content,
                   double latitude, double longitude, String location_name) {
        this.product_id = product_id;
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location_name = location_name;
    }

    public Message() {}

    // getter 추가!
    public int getProduct_id() { return product_id; }
    public int getSender_id() { return sender_id; }
    public int getReceiver_id() { return receiver_id; }
    public String getContent() { return content; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getLocation_name() { return location_name; }
    public long getTimestamp() {
        return timestamp;
    }
}
