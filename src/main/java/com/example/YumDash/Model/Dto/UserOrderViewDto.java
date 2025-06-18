package com.example.YumDash.Model.Dto;

import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.Food.OrderItem;
import com.example.YumDash.Model.User.UserOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserOrderViewDto {
    private boolean reviewed;
    private int id;
    private int providerId;
    private String restaurantName;
    private String restaurantImageUrl;
    private LocalDateTime orderDate;
    private String phoneNumber;
    private double amount;
    private String status;
    private String userEmail;
    private String foodProviderName;
    private String address;
    private String paymentMethod;
    private String deliveryMethod;
    private String comment;
    private List<OrderItemDto> orderItems;
}
