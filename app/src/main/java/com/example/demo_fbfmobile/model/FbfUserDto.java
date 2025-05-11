package com.example.demo_fbfmobile.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FbfUserDto {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
    private String fbfRole;
}
