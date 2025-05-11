package com.example.YumDash.Repository;

import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<UserOrder, Integer> {
    List<UserOrder> findByUser(User user);
    List<UserOrder> findByFoodProvider(FoodProvider provider);
}
