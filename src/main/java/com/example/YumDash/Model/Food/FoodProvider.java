package com.example.YumDash.Model.Food;

import com.example.YumDash.Model.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "food_provider")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String imageurl;

    @Column(unique = true)
    private String email;

    private String address;
    private double latitude;
    private double longitude;
    private String phoneNumber;



    @OneToMany(mappedBy = "foodProvider", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodProduct> foodProducts;


    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
