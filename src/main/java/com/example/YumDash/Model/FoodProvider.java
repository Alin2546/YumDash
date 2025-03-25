package com.example.YumDash.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "food_provider")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String imageurl;
    private String county;
}
