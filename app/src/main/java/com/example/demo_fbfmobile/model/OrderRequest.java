package com.example.demo_fbfmobile.model;

import java.util.List;

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
public class OrderRequest {
    private String phoneNumber;
    private String address;
    private List<Long> selectedCartItemIds;
    private String discountCode;
}
