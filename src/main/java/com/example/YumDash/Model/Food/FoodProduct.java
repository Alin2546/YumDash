package com.example.YumDash.Model.Food;

import com.example.YumDash.Model.Category;
import com.example.YumDash.Model.User.UserOrder;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;

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

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_order_id")
    private UserOrder userOrder;

    private String name;
    private double price;
    private String imageurl;
    private Category category;

}

