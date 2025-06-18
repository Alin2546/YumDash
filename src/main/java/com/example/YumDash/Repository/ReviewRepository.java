package com.example.YumDash.Repository;

import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.Food.Review;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByFoodProviderId(int foodProviderId);
    Optional<Review> findByUserAndFoodProviderAndOrder(User user, FoodProvider provider, UserOrder order);
    List<Review> findAllByFoodProviderId(int foodProviderId);

}