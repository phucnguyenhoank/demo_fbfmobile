package com.example.demo_fbfmobile.model;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private List<FoodSizeDto> sizes;
}
