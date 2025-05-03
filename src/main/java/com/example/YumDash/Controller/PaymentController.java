package com.example.YumDash.Controller;

import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Repository.OrderRepo;
import com.example.YumDash.Repository.UserRepo;
import com.example.YumDash.Service.FoodService.CartService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private final CartService cartService;
    private final FoodProductRepo foodProductRepo;
    private final UserRepo userRepo;
    private final OrderRepo orderRepo;

    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session) throws StripeException {
        Map<Integer, Integer> cart = cartService.getCartFromSession(session);
        Map<Integer, FoodProduct> foodProductMap = new HashMap<>();
        for (Integer productId : cart.keySet()) {
            foodProductMap.put(productId, foodProductRepo.findById(productId).orElse(null));
        }
        double total = cartService.calculateTotal(session, foodProductMap);


        com.stripe.Stripe.apiKey = stripeSecretKey;


        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (total * 100))
                .setCurrency("ron")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);


        model.addAttribute("clientSecret", paymentIntent.getClientSecret());
        model.addAttribute("total", total);

        return "payment";
    }


    @GetMapping("/payment/success")
    public String paymentSuccess(HttpSession session, Principal principal) {
        if (principal != null) {
            String email = null;

            if (principal instanceof OAuth2AuthenticationToken oauthToken) {
                OAuth2User oauthUser = oauthToken.getPrincipal();
                email = oauthUser.getAttribute("email");
            } else {

                email = principal.getName();
            }


            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Userul nu a fost găsit cu emailul"));


            Map<Integer, Integer> cart = cartService.getCartFromSession(session);
            Map<Integer, FoodProduct> foodMap = new HashMap<>();
            for (Integer id : cart.keySet()) {
                foodMap.put(id, foodProductRepo.findById(id).orElse(null));
            }

            double total = cartService.calculateTotal(session, foodMap);
            String address = (String) session.getAttribute("address");


            UserOrder order = new UserOrder();
            order.setUser(user);
            order.setAmount((int) total);
            order.setOrderDate(LocalDate.now().toString());
            order.setStatus("Comanda trimisa");
            order.setAddress(address);

            orderRepo.save(order);


            cartService.clearCart(session);
        }

        return "paymentSuccess";
    }



    @GetMapping("/payment/cancel")
    public String paymentCancel() {
        return "paymentCancel";
    }
}
