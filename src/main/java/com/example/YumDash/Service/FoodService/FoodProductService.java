package com.example.YumDash.Service.FoodService;

import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Repository.FoodProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodProductService {
    private final FoodProductRepo foodProductRepo;

    public List<FoodProduct> getProductsByProviderId(int providerId) {
        return foodProductRepo.findByFoodProviderId(providerId);
    }

//    public void addProductToCart(int id){
//
//    }
}
