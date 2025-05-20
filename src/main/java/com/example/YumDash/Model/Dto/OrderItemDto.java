package com.example.YumDash.Model.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderItemDto {
    private String productName;
    private int quantity;
}
