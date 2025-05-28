package com.example.YumDash.Controller;

import com.example.YumDash.Model.Category;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Service.FoodService.FoodProductService;
import com.example.YumDash.Service.FoodService.FoodProviderService;
import com.example.YumDash.Util.AuthUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class FoodController {


    private final FoodProviderService foodProviderService;
    private final FoodProductService foodProductService;

    @GetMapping("/products")
    public String getProductsByProvider(
            @RequestParam("providerId") int providerId,
            @RequestParam(name = "category", required = false) Category category,
            Model model
    ) {
        FoodProvider provider = foodProviderService.findById(providerId);

        List<FoodProduct> products = (category != null)
                ? foodProductService.getProductsByProviderIdAndCategory(providerId, category)
                : foodProductService.getProductsByProviderId(providerId);

        Set<Category> availableCategoriesSet = products.stream()
                .map(FoodProduct::getCategory)
                .collect(Collectors.toSet());


        List<Category> availableCategories = new ArrayList<>(availableCategoriesSet);

        List<Category> sortedCategories = foodProductService.sortCategoriesCustom(availableCategories);

        model.addAttribute("foodProvider", provider);
        model.addAttribute("products", products);
        model.addAttribute("categories", sortedCategories);
        model.addAttribute("selectedCategory", category);

        return "restaurantDetails";
    }


    @GetMapping("/search")
    public String search(@RequestParam String keyword,
                         Model model,
                         HttpSession session) {

        String address = (String) session.getAttribute("savedAddress");
        model.addAttribute("address", address);
        model.addAttribute("keyword", keyword);

        List<FoodProvider> allMatchingProviders = foodProviderService.searchFoodProviders(keyword);

        List<FoodProvider> availableProviders = new ArrayList<>();
        List<FoodProvider> unavailableProviders = new ArrayList<>();


        if (address != null && !address.isEmpty()) {
            for (FoodProvider provider : allMatchingProviders) {
                if (foodProviderService.isWithinDeliveryRange(provider, address)) {
                    availableProviders.add(provider);
                } else {
                    unavailableProviders.add(provider);
                }
            }
        } else {
            availableProviders = allMatchingProviders;
        }
        model.addAttribute("availableProviders", availableProviders);
        model.addAttribute("unavailableProviders", unavailableProviders);
        return "searchResults";
    }
}



