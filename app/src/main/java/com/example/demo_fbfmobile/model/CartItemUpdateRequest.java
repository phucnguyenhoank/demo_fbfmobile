package com.example.demo_fbfmobile.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemUpdateRequest {
    private Long cartItemId;
    private Integer newQuantity;
    private String newSize; // Expected values: "S", "M", "L", "XL"
}
