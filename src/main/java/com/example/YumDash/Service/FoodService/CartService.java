package com.example.YumDash.Service.FoodService;

import com.example.YumDash.Model.Food.FoodProduct;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CartService {

    public static final String CART_SESSION_KEY = "cart";


    public void addToCart(HttpSession session, FoodProduct foodProduct) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }


        Integer foodProductId = foodProduct.getId();
        cart.put(foodProductId, cart.getOrDefault(foodProductId, 0) + 1);
    }


    public Map<Integer, Integer> getCartFromSession(HttpSession session) {
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }


    public void removeFromCart(HttpSession session, Integer foodProductId) {
        Map<Integer, Integer> cart = getCartFromSession(session);
        cart.remove(foodProductId);
    }


    public void clearCart(HttpSession session) {
        Map<Integer, Integer> cart = getCartFromSession(session);
        cart.clear();
    }


    public double calculateTotal(HttpSession session, Map<Integer, FoodProduct> foodProductMap) {
        Map<Integer, Integer> cart = getCartFromSession(session);
        return cart.entrySet().stream()
                .mapToDouble(entry -> foodProductMap.get(entry.getKey()).getPrice() * entry.getValue())
                .sum();
    }


    public int getQuantity(HttpSession session, Integer foodProductId) {
        Map<Integer, Integer> cart = getCartFromSession(session);
        return cart.getOrDefault(foodProductId, 0);
    }
}
