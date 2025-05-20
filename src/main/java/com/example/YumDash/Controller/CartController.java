package com.example.YumDash.Controller;
import com.example.YumDash.Model.Dto.CheckoutDto;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Service.FoodService.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final FoodProductRepo foodProductRepo;



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
        UserOrder draftOrder = (UserOrder) session.getAttribute("draftOrder");

        if (draftOrder != null) {
            CheckoutDto checkoutDto = cartService.convertOrderToCheckoutDto(draftOrder);
            cartService.populateCartViewModel(model, session, checkoutDto);
            session.removeAttribute("draftOrder");
        } else {
            cartService.populateCartViewModel(model, session, null);
        }

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
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        cart.put(productId, cart.getOrDefault(productId, 0) + 1);
        session.setAttribute("cart", cart);
        return "redirect:/cart/view";
    }




}
