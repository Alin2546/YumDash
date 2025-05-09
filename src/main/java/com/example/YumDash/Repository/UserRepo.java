package com.example.YumDash.Repository;

import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);


    List<User> findByFoodProvider(FoodProvider provider);
}
