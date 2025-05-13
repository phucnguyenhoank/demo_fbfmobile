package com.example.demo_fbfmobile.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDetailDto {
    private Long id;
    private String size;
    private Double discountedPrice;
    private Double discountPercentage;
    private Integer quantity;
    private String foodName;
    private String imageUrl;
    private String createdAt;
}
