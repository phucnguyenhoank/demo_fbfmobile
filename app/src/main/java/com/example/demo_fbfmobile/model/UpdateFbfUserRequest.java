package com.example.demo_fbfmobile.model;

public class UpdateUserRequest {
    private String phoneNumber;
    private String address;
    public UpdateUserRequest(String phoneNumber, String address) {
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
