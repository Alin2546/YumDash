package com.example.YumDash.Service.FoodService;

import com.example.YumDash.Model.Dto.CheckoutDto;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {

    public static final String CART_SESSION_KEY = "cart";
    private final UserService userService;


    private final FoodProviderService foodProviderService;
    private final FoodProductService foodProductService;

    public String getRestaurantNameFromCart(Map<Integer, Integer> cart) {
        if (cart == null || cart.isEmpty()) {
            return null;
        }

        Integer firstProductId = cart.keySet().iterator().next();

        FoodProvider foodProvider = foodProviderService.getFoodProviderByProductId(firstProductId);

        return foodProvider != null ? foodProvider.getName() : "Restaurant necunoscut";
    }

    public Map<Integer, Integer> getCartFromSession(HttpSession session) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    public double calculateTotal(HttpSession session, Map<Integer, FoodProduct> foodProductMap) {
        Map<Integer, Integer> cart = getCartFromSession(session);
        return cart.entrySet().stream()
                .mapToDouble(entry -> foodProductMap.get(entry.getKey()).getPrice() * entry.getValue())
                .sum();
    }

    public void populateCartViewModel(Model model, HttpSession session, CheckoutDto checkoutDto, Principal principal) {
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) cart = new HashMap<>();

        Map<Integer, FoodProduct> foodProductMap = foodProductService.getAllFoodProducts();

        double subtotal = calculateTotal(session, foodProductMap);

        double discount = 0.0;
        boolean eligibleForDiscount = false;


        if (principal != null) {
            User user;
            if (principal instanceof OAuth2AuthenticationToken oauthToken) {
                String email = oauthToken.getPrincipal().getAttribute("email");
                user = userService.findByEmail(email).orElse(null);
            } else {
                String email = principal.getName();
                user = userService.findByEmail(email).orElse(null);
            }
            if (user != null && user.isPhoneVerified() && !user.isDiscountUsed()) {
                eligibleForDiscount = true;
                discount = subtotal * 0.5;
                subtotal -= discount;
            }
        }

        double deliveryFee = subtotal >= 100.0 ? 0.0 : 9.99;
        double packagingFee = 2.50;
        double serviceFee = 1.20;
        double total = subtotal + deliveryFee + packagingFee + serviceFee;

        String userAddress = (String) session.getAttribute("savedAddress");
        Double savedLatitude = (Double) session.getAttribute("latitude");
        Double savedLongitude = (Double) session.getAttribute("longitude");

        if (userAddress == null) userAddress = "";
        if (savedLatitude == null) savedLatitude = 47.1585;
        if (savedLongitude == null) savedLongitude = 27.5867;

        Integer selectedRestaurantId = (Integer) session.getAttribute("selectedRestaurantId");

        model.addAttribute("deliveryAddress", userAddress);
        model.addAttribute("latitude", savedLatitude);
        model.addAttribute("longitude", savedLongitude);
        model.addAttribute("cart", cart);
        model.addAttribute("foodProductMap", foodProductMap);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("deliveryFee", deliveryFee);
        model.addAttribute("packagingFee", packagingFee);
        model.addAttribute("serviceFee", serviceFee);
        model.addAttribute("discount", discount);
        model.addAttribute("eligibleForDiscount", eligibleForDiscount);
        model.addAttribute("total", total);
        model.addAttribute("selectedRestaurantId", selectedRestaurantId);

        if (selectedRestaurantId != null) {
            FoodProvider selectedRestaurant = foodProviderService.getFoodProviderById(selectedRestaurantId);
            if (selectedRestaurant != null) {
                model.addAttribute("restaurantName", selectedRestaurant.getName());
                model.addAttribute("restaurantImage", selectedRestaurant.getImageurl());
            }
        }

        if (checkoutDto == null) {
            checkoutDto = new CheckoutDto();
            checkoutDto.setDeliveryAddress(userAddress);
        }
        if (principal != null) {
            User user;
            if (principal instanceof OAuth2AuthenticationToken oauthToken) {
                String email = oauthToken.getPrincipal().getAttribute("email");
                user = userService.findByEmail(email).orElse(null);
            } else {
                String email = principal.getName();
                user = userService.findByEmail(email).orElse(null);
            }
            if (user != null && user.getPhoneNumber() != null) {
                String phone = user.getPhoneNumber();
                if (phone.startsWith("+4")) {
                    phone = phone.substring(2);
                }
                checkoutDto.setPhoneNumber(phone);
            }
        }
        model.addAttribute("checkoutDto", checkoutDto);
    }


    public CheckoutDto convertOrderToCheckoutDto(UserOrder order) {
        CheckoutDto dto = new CheckoutDto();
        dto.setDeliveryAddress(order.getAddress());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setDeliveryMethod(order.getDeliveryMethod());
        dto.setComment(order.getComment());
        dto.setPhoneNumber(order.getPhoneNumber());
        return dto;
    }




}
