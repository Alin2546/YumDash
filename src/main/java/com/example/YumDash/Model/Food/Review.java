package com.example.YumDash.Model.Food;

import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(5)
    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "food_provider_id")
    private FoodProvider foodProvider;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private UserOrder order;

    private LocalDateTime createdAt;


}
