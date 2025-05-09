package com.example.YumDash.Model.User;

import com.example.YumDash.Model.Food.FoodProvider;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private String provider;
    private String address;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserOrder> userOrders;

    @OneToOne
    @JoinColumn(name = "food_provider_id")
    private FoodProvider foodProvider;

}
