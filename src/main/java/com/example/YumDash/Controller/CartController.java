package com.example.YumDash.Controller;

import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Service.FoodService.CartService;
import com.example.YumDash.Service.FoodService.FoodProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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


        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);  // Inițializează coșul în sesiune
        }


        cart.put(foodProductId, cart.getOrDefault(foodProductId, 0) + 1);

        return "redirect:/cart/view";
    }


    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }


        Map<Integer, FoodProduct> foodProductMap = foodProductService.getAllFoodProducts();

//        double total = cartService.calculateTotal(cart, foodProductMap);

        model.addAttribute("cart", cart);
        model.addAttribute("foodProductMap", foodProductMap);
//        model.addAttribute("total", total);

        return "cartView";
    }


    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("foodProductId") Integer foodProductId, HttpSession session) {
        cartService.removeFromCart(session, foodProductId);
        return "redirect:/cart/view";
    }


    @PostMapping("/clear")
    public String clearCart(HttpSession session) {
        cartService.clearCart(session);
        return "redirect:/cart/view";
    }
}
