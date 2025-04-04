package com.example.YumDash.Controller;


import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Service.FoodService.FoodProviderService;
import com.example.YumDash.Service.GoogleMapsService;
import com.example.YumDash.Service.SecurityService.MyUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {


    private final GoogleMapsService googleMapsService;
    private final FoodProviderService foodProviderService;


    @GetMapping
    public String homePage(Model model) {
        return "home";
    }

    @GetMapping("/chooseAddress")
    public String getAddress(@RequestParam String address, Model model) {
        if (address != null && !address.isEmpty()) {
            List<String> addresses = googleMapsService.getFormattedAddress(address);
            model.addAttribute("addressSuggestions", addresses);
        }
        return "AddressChooser";
    }

    @GetMapping("/getFoodPage")
    public String showFoodPage(Model model, Authentication authentication, @RequestParam(required = false) String address, HttpSession session) {
        if (address != null && !address.isEmpty()) {
            session.setAttribute("savedAddress", address);
        } else {
            address = (String) session.getAttribute("savedAddress");
        }
        if (authentication != null) {
            MyUser myUser = (MyUser) authentication.getPrincipal();
            model.addAttribute("loggedInUser", myUser.getUsername());
        }
        if (address != null && !address.isEmpty()) {
            List<FoodProvider> nearbyRestaurants = foodProviderService.getNearbyRestaurants(address);
            model.addAttribute("foodProviders", nearbyRestaurants);
        }
        return "foodPage";
    }
}

