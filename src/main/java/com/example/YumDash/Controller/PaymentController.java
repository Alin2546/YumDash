package com.example.YumDash.Controller;

import com.example.YumDash.Model.Dto.CheckoutDto;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Repository.OrderRepo;
import com.example.YumDash.Repository.UserRepo;
import com.example.YumDash.Service.FoodService.CartService;
import com.example.YumDash.Service.OrderService;
import com.example.YumDash.Service.UserService;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final UserService userService;
    private final OrderService orderService;

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
        model.addAttribute("checkoutDTO", new CheckoutDto());

        return "payment";
    }

    @PostMapping("/checkout")
    public String processCheckout(
            @ModelAttribute CheckoutDto checkoutDTO,
            Model model,
            Principal principal,
            HttpSession session) {


        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User nu a fost găsit"));


        session.setAttribute("checkoutDTO", checkoutDTO);


        UserOrder order = new UserOrder();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now().toString());
        order.setAddress(checkoutDTO.getAddress());
        order.setPaymentMethod(checkoutDTO.getPaymentMethod());
        order.setDeliveryMethod(checkoutDTO.getDeliveryMethod());
        order.setComment(checkoutDTO.getComment());
        order.setNeedCutlery(checkoutDTO.isNeedCutlery());


        session.setAttribute("order", order);


        return "redirect:/payment/success";
    }

    @GetMapping("/payment/success")
    public String paymentSuccess(HttpSession session, Principal principal) {

        UserOrder order = (UserOrder) session.getAttribute("order");
        if (order == null) {
            return "paymentCancel";
        }

        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User nu a fost găsit"));

        order.setUser(user);
        order.setStatus("Comanda trimisa");

        orderService.createOrder(order);

        cartService.clearCart(session);
        session.removeAttribute("order");
        session.removeAttribute("checkoutDTO");

        return "paymentSuccess";
    }

    @GetMapping("/payment/cancel")
    public String paymentCancel() {
        return "paymentCancel";
    }
}
