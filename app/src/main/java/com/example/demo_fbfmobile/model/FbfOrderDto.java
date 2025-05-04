package com.example.demo_fbfmobile.model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FbfOrderDto {
    private Long id;
    private Double discountedTotalPrice;
    private String phoneNumber;
    private String address;
    private String createdAt;
    private Long fbfUserId;
    private DiscountCodeDto discountCode;
    private List<OrderItemDto> items;
}
