package com.example.YumDash.Model.Food;


import com.example.YumDash.Model.User.UserOrder;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private UserOrder order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private FoodProduct product;

    private int quantity;
}
