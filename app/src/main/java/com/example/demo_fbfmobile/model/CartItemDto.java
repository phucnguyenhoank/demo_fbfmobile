package com.example.demo_fbfmobile.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Long id;
    private FoodSizeDto foodSize;
    private Double price;
    private Double discountPercentage;
    private Integer quantity;
    private Long cartId;
}
