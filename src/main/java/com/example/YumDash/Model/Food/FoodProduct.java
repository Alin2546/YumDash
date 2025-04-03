package com.example.YumDash.Model.Food;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "food_product")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "food_provider_id")
    private FoodProvider foodProvider;

    private String name;
    private String price;
    private String imageurl;
}

