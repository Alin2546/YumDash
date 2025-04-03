package com.example.YumDash.Model.Food;

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
    private double latitude;
    private double longitude;
    @OneToMany(mappedBy = "foodProvider", cascade = CascadeType.ALL)
    private List<FoodProduct> foodProducts;

}
