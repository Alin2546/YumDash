package com.example.YumDash.Repository;

import com.example.YumDash.Model.Food.FoodProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodProviderRepo extends JpaRepository<FoodProvider, Integer> {
    List<FoodProvider> findByNameContainingIgnoreCase(String keyword);

    Optional<FoodProvider> findByEmail(String email);


}
