package com.example.YumDash.Model.Dto;

import com.example.YumDash.Model.Food.FoodProduct;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodProductDto {
    private Integer id;
    private String name;
    private double price;
    private int quantity;

    public FoodProductDto(FoodProduct foodProduct, int quantity) {
        this.id = foodProduct.getId();
        this.name = foodProduct.getName();
        this.price = foodProduct.getPrice();
        this.quantity = quantity;
    }
}
