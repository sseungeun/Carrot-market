package com.example.carrot.model;

public class Message {
    private String id;
    private String productId;
    private String senderId;
    private String receiverId;
    private String message;
    private String timestamp;

    public String getId() { return id; }
    public String getProductId() { return productId; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
}
