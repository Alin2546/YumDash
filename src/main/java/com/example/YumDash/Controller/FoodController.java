package com.example.YumDash.Controller;

import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Service.FoodService;
import com.example.YumDash.Service.SecurityService.MyUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FoodController {


    private final FoodService foodService;

    @GetMapping("/products")
    public String getProductsByProvider(@RequestParam("providerId") int providerId, Model model) {
        model.addAttribute("products", foodService.getProductsByProviderId(providerId));
        return "restaurantDetails";
    }


    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<FoodProvider> providers = foodService.searchFoodProviders(keyword);
        model.addAttribute("providers", providers);
        return "searchResults";
    }
}
