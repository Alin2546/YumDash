package com.example.YumDash.Service.FoodService;

import com.example.YumDash.Model.Category;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Repository.FoodProviderRepo;
import com.example.YumDash.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public List<FoodProduct> getProductsByProviderIdAndCategory(int providerId, Category category) {
        return foodProductRepo.findByFoodProviderIdAndCategory(providerId, category);
    }
}
