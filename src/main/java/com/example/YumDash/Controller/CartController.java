package com.example.YumDash.Controller;

import com.example.YumDash.Model.Dto.CheckoutDto;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Service.FoodService.CartService;
import com.example.YumDash.Service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final FoodProductRepo foodProductRepo;
    private final UserService userService;


    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addToCart(@RequestParam("foodProductId") Integer foodProductId,
                                            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
                                            HttpSession session) {

        if (quantity <= 0) {
            return ResponseEntity.badRequest().body("Cantitatea trebuie să fie cel puțin 1.");
        }

        FoodProduct foodProduct = foodProductRepo.findById(foodProductId)
                .orElseThrow(() -> new RuntimeException("Produsul nu a fost găsit."));

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
            return ResponseEntity.badRequest().body("Poți comanda doar de la un singur restaurant.");
        }

        cart.put(foodProductId, cart.getOrDefault(foodProductId, 0) + quantity);

        return ResponseEntity.ok("Produs adăugat în coș.");
    }


    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model, Principal principal) {
        UserOrder draftOrder = (UserOrder) session.getAttribute("draftOrder");

        if (draftOrder != null) {
            CheckoutDto checkoutDto = cartService.convertOrderToCheckoutDto(draftOrder);
            cartService.populateCartViewModel(model, session, checkoutDto, principal);
            session.removeAttribute("draftOrder");
        } else {
            cartService.populateCartViewModel(model, session, null, principal);
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
            }
            if (user != null) {
                boolean eligibleForDiscount = user.isPhoneVerified() && !user.isDiscountUsed();
                model.addAttribute("eligibleForDiscount", eligibleForDiscount);
            }

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

        if (cart.isEmpty()) {
            session.removeAttribute("selectedRestaurantId");
            session.removeAttribute("selectedRestaurantName");
            session.removeAttribute("restaurantImage");
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
