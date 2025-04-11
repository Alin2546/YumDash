package com.example.YumDash.Controller;

import com.example.YumDash.Model.Food.FoodProvider;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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



