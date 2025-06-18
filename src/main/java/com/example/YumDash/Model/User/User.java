package com.example.YumDash.Model.User;

import com.example.YumDash.Model.Food.Review;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String address;
    private boolean isActive;
    private String verificationCode;
    private boolean isDiscountUsed = false;
    private String phoneNumber;
    private boolean phoneVerified = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserOrder> userOrders;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> authProviders = new HashSet<>();
}





