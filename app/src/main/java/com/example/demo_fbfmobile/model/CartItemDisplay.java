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
public class CartItemDisplay {
    private Long id;
    private String foodName;
    private String foodImageUrl;
    private String size;
    private Double price;
    private Double discountPercentage;
    private Integer quantity;
}
