package com.example.YumDash.Controller;


import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Service.FoodService.CartService;
import com.example.YumDash.Service.FoodService.FoodProviderService;
import com.example.YumDash.Service.FoodService.ReviewService;
import com.example.YumDash.Service.GoogleMapsService;
import com.example.YumDash.Service.UserService;
import com.example.YumDash.Util.AuthUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HomeController {


    private final GoogleMapsService googleMapsService;
    private final FoodProviderService foodProviderService;
    private final CartService cartService;
    private final ReviewService reviewService;
    private final UserService userService;


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
            User user = userService.findByEmail(username).orElse(null);
            boolean eligibleForDiscount = false;
            if (user != null) {
                eligibleForDiscount = user.isPhoneVerified() && !user.isDiscountUsed();
            }
            model.addAttribute("eligibleForDiscount", eligibleForDiscount);
        } else {
            model.addAttribute("eligibleForDiscount", false);
        }

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        int cartSize = cart.size();
        model.addAttribute("cartSize", cartSize);

        if (address != null && !address.isEmpty()) {
            List<FoodProvider> topProviders = foodProviderService.getNearbyRestaurants(address).stream()
                    .filter(fp -> fp.getUser() != null && Boolean.TRUE.equals(fp.getUser().isActive()))
                    .filter(fp -> reviewService.getAverageRating(fp.getId()) >= 4.0)
                    .collect(Collectors.toList());
            model.addAttribute("topProviders", topProviders);

            List<FoodProvider> nearbyRestaurants = foodProviderService.getNearbyRestaurants(address);
            List<FoodProvider> activeRestaurants = nearbyRestaurants.stream()
                    .filter(fp -> fp.getUser() != null && Boolean.TRUE.equals(fp.getUser().isActive()))
                    .collect(Collectors.toList());

            model.addAttribute("foodProviders", activeRestaurants);


            List<List<FoodProvider>> groupedProviders = new ArrayList<>();
            for (int i = 0; i < topProviders.size(); i += 3) {
                int end = Math.min(i + 3, topProviders.size());
                groupedProviders.add(topProviders.subList(i, end));
            }
            model.addAttribute("groupedProviders", groupedProviders);
        }

        String restaurantName = cartService.getRestaurantNameFromCart(cart);
        model.addAttribute("cartRestaurantName", restaurantName);

        return "foodPage";
    }
}

