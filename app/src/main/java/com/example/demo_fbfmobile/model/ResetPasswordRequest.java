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
public class ResetPasswordRequest {
    private String email;
    private String otp;
    private String newPassword;
}
