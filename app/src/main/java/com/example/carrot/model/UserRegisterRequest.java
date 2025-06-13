package com.example.carrot.model;

public class UserRegisterRequest {
    private String username;
    private String name;
    private String email;
    private String password;

    public UserRegisterRequest() {}

    public UserRegisterRequest(String username, String name, String email, String password) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}
