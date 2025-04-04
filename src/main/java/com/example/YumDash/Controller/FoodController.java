package com.example.YumDash.Controller;

import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Service.FoodService.FoodProductService;
import com.example.YumDash.Service.FoodService.FoodProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class FoodController {


    private final FoodProviderService foodProviderService;
    private final FoodProductService foodProductService;

    @GetMapping("/products")
    public String getProductsByProvider(@RequestParam("providerId") int providerId, Model model) {
        model.addAttribute("products", foodProductService.getProductsByProviderId(providerId));
        return "restaurantDetails";
    }


    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<FoodProvider> providers = foodProviderService.searchFoodProviders(keyword);
        model.addAttribute("providers", providers);
        return "searchResults";
    }

//    @GetMapping("/addToCart"){
//        public String addProductToCart(){
//
//        }
//    }
}
