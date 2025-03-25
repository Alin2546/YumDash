package com.example.YumDash.Controller;

import com.example.YumDash.Model.FoodProvider;
import com.example.YumDash.Service.FoodProviderService;
import com.example.YumDash.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class FoodProviderController {


    private final FoodProviderService foodProviderService;

    @GetMapping("restaurant/{id}")
    public String getRestaurantDetails(@PathVariable int id, Model model) {
        FoodProvider foodProvider = foodProviderService.findById(id);
        model.addAttribute("foodProvider", foodProvider);
        return "restaurantDetails";
    }
}
