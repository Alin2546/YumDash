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
}
