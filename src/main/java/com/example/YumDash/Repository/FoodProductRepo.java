package com.example.YumDash.Repository;

import com.example.YumDash.Model.Category;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodProductRepo extends JpaRepository<FoodProduct,Integer> {
    List<FoodProduct> findByFoodProvider(FoodProvider provider);
    List<FoodProduct> findByFoodProviderId(int providerId);
    List<FoodProduct> findByFoodProviderIdAndCategory(int providerId, Category category);
}
