package com.example.YumDash.Controller;

import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Service.FoodService.CartService;
import com.example.YumDash.Service.FoodService.FoodProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final FoodProductRepo foodProductRepo;
    private final FoodProductService foodProductService;




    @PostMapping("/add")
    public String addToCart(@RequestParam("foodProductId") Integer foodProductId, HttpSession session) {
        FoodProduct foodProduct = foodProductRepo.findById(foodProductId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
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

        Map<Integer, FoodProduct> foodProductMap = foodProductService.getAllFoodProducts();

     double total = cartService.calculateTotal(session, foodProductMap);

        model.addAttribute("cart", cart);
        model.addAttribute("foodProductMap", foodProductMap);
     model.addAttribute("total", total);

        return "cartView";
    }


    @GetMapping("/decrease/{productId}")
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

    @GetMapping("/increase/{productId}")
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
}
