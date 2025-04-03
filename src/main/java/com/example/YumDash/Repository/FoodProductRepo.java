package com.example.YumDash.Repository;

import com.example.YumDash.Model.Food.FoodProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodProductRepo extends JpaRepository<FoodProduct,Integer> {
    List<FoodProduct> findByFoodProviderId(int foodProviderId);
}
