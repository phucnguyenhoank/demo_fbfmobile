package com.example.demo_fbfmobile.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCodeDto {
    private Long id;
    private String code;
    private Double discountPercentage;
    private String expirationDate;
}
