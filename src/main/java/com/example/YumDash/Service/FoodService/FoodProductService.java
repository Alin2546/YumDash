package com.example.YumDash.Service.FoodService;

import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Repository.FoodProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FoodProductService {
    private final FoodProductRepo foodProductRepo;

    public List<FoodProduct> getProductsByProviderId(int providerId) {
        return foodProductRepo.findByFoodProviderId(providerId);
    }

    public Map<Integer, FoodProduct> getAllFoodProducts() {

        List<FoodProduct> foodProducts =foodProductRepo.findAll();
        Map<Integer, FoodProduct> foodProductMap = new HashMap<>();

        for (FoodProduct foodProduct : foodProducts) {
            foodProductMap.put(foodProduct.getId(), foodProduct);
        }

        return foodProductMap;
    }
}
