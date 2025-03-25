package com.example.YumDash.Controller;

import com.example.YumDash.Model.Dto.UserLoginDto;
import com.example.YumDash.Model.FoodProvider;
import com.example.YumDash.Model.User;
import com.example.YumDash.Repository.FoodProviderRepo;
import com.example.YumDash.Service.FoodProviderService;
import com.example.YumDash.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {


    private final FoodProviderService foodProviderService;

    @GetMapping
    public String homePage() {
        return "home";
    }


    @GetMapping("/submit/address")
    public String getFoodPage(Model model) {
        List<FoodProvider> foodProviders = foodProviderService.getAllProviders();
        model.addAttribute("foodProviders", foodProviders);
        return "foodPage";
    }

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<FoodProvider> providers = foodProviderService.searchFoodProviders(keyword);
        model.addAttribute("providers", providers);
        return "searchResults";
    }

    @GetMapping("/currentLocation")
    public String searchByLocation(@RequestParam("location") String location, Model model) {
        List<FoodProvider> foodProviders = foodProviderService.findByCounty(location);
        model.addAttribute("restaurants", foodProviders);
        return "currentLocationProviders";
    }
}

