package com.example.demo_fbfmobile.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
    private String otp; // dùng khi gửi để xác thực

}
