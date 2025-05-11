package com.example.YumDash.Model.Dto;

import com.example.YumDash.Model.Food.FoodProduct;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CheckoutDto {
    private String address;
    private String block;
    private String staircase;
    private String apartment;
    private String comment;
    private String paymentMethod;
    private String deliveryMethod;
    private List<FoodProduct> products;
    private String status = "Comanda Trimisa";

}
