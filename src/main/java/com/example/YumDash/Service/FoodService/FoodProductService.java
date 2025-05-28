package com.example.YumDash.Service.FoodService;

import com.example.YumDash.Model.Category;
import com.example.YumDash.Model.Dto.ProductUpdateDto;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Repository.FoodProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        List<FoodProduct> foodProducts = foodProductRepo.findAll();
        Map<Integer, FoodProduct> foodProductMap = new HashMap<>();

        for (FoodProduct foodProduct : foodProducts) {
            foodProductMap.put(foodProduct.getId(), foodProduct);
        }

        return foodProductMap;
    }

    public List<FoodProduct> getProductsByProviderId(int providerId) {
        return foodProductRepo.findByFoodProviderId(providerId);
    }

    public List<FoodProduct> getProductsByProviderIdAndCategory(int providerId, Category category) {
        return foodProductRepo.findByFoodProviderIdAndCategory(providerId, category);
    }

    public List<Category> sortCategoriesCustom(List<Category> categories) {
        return categories.stream()
                .sorted(Comparator.comparingInt(cat -> {
                    return switch (cat) {
                        case PIZZA, BURGERI, SUSHI, SHAORMA, PASTE, SALATE, SUPE, GUSTARI -> 0;
                        case MIC_DEJUN, VEGAN, VEGETARIAN, TRADITIONAL_ROMANESC, INTERNATIONAL -> 1;
                        case DESERTURI -> 2;
                        case BAUTURI_RACORITOARE, SUCURI_NATURALE, CAFEA, CEAI, SMOOTHIE -> 3;
                        default -> 4;
                    };
                }))
                .collect(Collectors.toList());
    }
}
