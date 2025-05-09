package com.example.YumDash.Model.User;

import jakarta.persistence.*;
import lombok.*;

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

    private String orderDate;
    private int amount;
    private String status;
    private String address;
    private String paymentMethod;
    private String deliveryMethod;
    private String comment;
    private boolean needCutlery;
}
