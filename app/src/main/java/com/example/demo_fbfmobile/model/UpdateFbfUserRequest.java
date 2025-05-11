package com.example.demo_fbfmobile.model;

public class UpdateFbfUserRequest {
    private String phoneNumber;
    private String address;
    public UpdateFbfUserRequest(String phoneNumber, String address) {
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
