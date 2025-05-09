package com.example.YumDash.Controller;

import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Service.FoodService.CartService;
import com.example.YumDash.Service.FoodService.FoodProductService;
import com.example.YumDash.Service.FoodService.FoodProviderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final FoodProductRepo foodProductRepo;
    private final FoodProductService foodProductService;
    private final FoodProviderService foodProviderService;


    @PostMapping("/add")
    public String addToCart(@RequestParam("foodProductId") Integer foodProductId, HttpSession session, RedirectAttributes redirectAttributes) {
        FoodProduct foodProduct = foodProductRepo.findById(foodProductId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        if (cart.isEmpty()) {
            session.removeAttribute("selectedRestaurantId");
        }

        Integer productRestaurantId = foodProduct.getFoodProvider().getId();
        Integer selectedRestaurantId = (Integer) session.getAttribute("selectedRestaurantId");



        if (selectedRestaurantId == null) {
            session.setAttribute("selectedRestaurantId", productRestaurantId);
        } else if (!selectedRestaurantId.equals(productRestaurantId)) {
            redirectAttributes.addFlashAttribute("error", "Poți comanda doar de la un singur restaurant.");


            return "redirect:/products?providerId=" + productRestaurantId +
                    (session.getAttribute("selectedCategory") != null ? "&category=" + session.getAttribute("selectedCategory") : "");
        }

        cart.put(foodProductId, cart.getOrDefault(foodProductId, 0) + 1);
        return "redirect:/cart/view";
    }


    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        String userAddress = (String) session.getAttribute("savedAddress");
        Double savedLatitude = (Double) session.getAttribute("latitude");
        Double savedLongitude = (Double) session.getAttribute("longitude");

        if (userAddress == null) userAddress = "";
        if (savedLatitude == null) savedLatitude = 47.1585;
        if (savedLongitude == null) savedLongitude = 27.5867;

        model.addAttribute("deliveryAddress", userAddress);
        model.addAttribute("latitude", savedLatitude);
        model.addAttribute("longitude", savedLongitude);

        Map<Integer, FoodProduct> foodProductMap = foodProductService.getAllFoodProducts();

        double subtotal = cartService.calculateTotal(session, foodProductMap);
        double deliveryFee = subtotal >= 100.0 ? 0.0 : 9.99;
        double packagingFee = 2.50;
        double serviceFee = 1.20;

        double total = subtotal + deliveryFee + packagingFee + serviceFee;
        model.addAttribute("cart", cart);
        model.addAttribute("foodProductMap", foodProductMap);
        model.addAttribute("total", total);


        model.addAttribute("subtotal", subtotal);
        model.addAttribute("deliveryFee", deliveryFee);
        model.addAttribute("packagingFee", packagingFee);
        model.addAttribute("serviceFee", serviceFee);

        Integer selectedRestaurantId = (Integer) session.getAttribute("selectedRestaurantId");


        if (selectedRestaurantId != null) {
            FoodProvider selectedRestaurant = foodProviderService.getFoodProviderById(selectedRestaurantId);
            if (selectedRestaurant != null) {
                model.addAttribute("restaurantName", selectedRestaurant.getName());
                model.addAttribute("restaurantImage", selectedRestaurant.getImageurl());
            }
        }

        model.addAttribute("selectedRestaurantId", selectedRestaurantId);
        return "cartView";
    }


    @PostMapping("/decrease/{productId}")
    public String decreaseItemQuantity(@PathVariable Integer productId, HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        if (cart.containsKey(productId)) {
            int currentQuantity = cart.get(productId);
            if (currentQuantity > 1) {
                cart.put(productId, currentQuantity - 1);
            } else {
                cart.remove(productId);
            }
        }

        session.setAttribute("cart", cart);
        return "redirect:/cart/view";
    }

    @PostMapping("/increase/{productId}")
    public String increaseItemQuantity(@PathVariable Integer productId, HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        cart.put(productId, cart.getOrDefault(productId, 0) + 1);
        session.setAttribute("cart", cart);
        return "redirect:/cart/view";
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session) {
        Map<Integer, Integer> cart = cartService.getCartFromSession(session);
        Map<Integer, FoodProduct> foodProductMap = new HashMap<>();
        for (Integer productId : cart.keySet()) {
            foodProductMap.put(productId, foodProductRepo.findById(productId).orElse(null));
        }
        double total = cartService.calculateTotal(session, foodProductMap);

        model.addAttribute("cart", cart);
        model.addAttribute("foodProductMap", foodProductMap);
        model.addAttribute("total", total);

        return "checkout";
    }


}
