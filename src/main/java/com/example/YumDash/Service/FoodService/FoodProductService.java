package com.example.YumDash.Service.FoodService;
import com.example.YumDash.Model.Category;
import com.example.YumDash.Model.Dto.ProductUpdateDto;
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

    public ProductUpdateDto toDto(FoodProduct product) {
        ProductUpdateDto dto = new ProductUpdateDto();
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setImageurl(product.getImageurl());
        dto.setCategory(product.getCategory());
        return dto;
    }

    public Map<Integer, FoodProduct> getAllFoodProducts() {

        List<FoodProduct> foodProducts =foodProductRepo.findAll();
        Map<Integer, FoodProduct> foodProductMap = new HashMap<>();

        for (FoodProduct foodProduct : foodProducts) {
            foodProductMap.put(foodProduct.getId(), foodProduct);
        }

        return foodProductMap;
    }

    public List<FoodProduct> getAvailableProductsByProviderId(int providerId) {
        return foodProductRepo.findByFoodProviderIdAndAvailable(providerId, true);
    }

    public List<FoodProduct> getAvailableProductsByProviderIdAndCategory(int providerId, Category category) {
        return foodProductRepo.findByFoodProviderIdAndCategoryAndAvailable(providerId, category, true);
    }
}
