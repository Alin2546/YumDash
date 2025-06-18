package com.example.YumDash.Model.User;

import com.example.YumDash.Model.Food.FoodProvider;

import com.example.YumDash.Model.Food.OrderItem;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "food_provider_id")
    private FoodProvider foodProvider;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private boolean reviewed = false;
    private LocalDateTime orderDate;
    private double amount;
    private String status;
    private String address;
    private String paymentMethod;
    private String deliveryMethod;
    private String comment;
    private String phoneNumber;
}
