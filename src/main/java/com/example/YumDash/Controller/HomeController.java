package com.example.YumDash.Controller;


import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Service.FoodService.FoodProviderService;
import com.example.YumDash.Service.GoogleMapsService;
import com.example.YumDash.Service.SecurityService.MyUser;
import com.example.YumDash.Util.AuthUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    public String showFoodPage(Model model, Authentication authentication,
                               @RequestParam(required = false) String address,
                               HttpSession session) {

        if (address != null && !address.isEmpty()) {
            session.setAttribute("savedAddress", address);
        } else {
            address = (String) session.getAttribute("savedAddress");
        }

        model.addAttribute("address", address);

        if (authentication != null) {
            String username = AuthUtils.extractUsername(authentication.getPrincipal());
            model.addAttribute("loggedInUser", username);
        }

        if (address != null && !address.isEmpty()) {
            List<FoodProvider> nearbyRestaurants = foodProviderService.getNearbyRestaurants(address);
            model.addAttribute("foodProviders", nearbyRestaurants);
        }
        return "foodPage";
    }

}

