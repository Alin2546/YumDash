package com.example.YumDash.Model.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutDto {
    private String deliveryMethod;
    private String paymentMethod;
    private String address;
    private String comment;
    private boolean needCutlery;
}
