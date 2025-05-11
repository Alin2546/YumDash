package com.example.YumDash.Controller;

import com.example.YumDash.Model.Dto.CheckoutDto;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.OrderItem;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Service.FoodService.CartService;
import com.example.YumDash.Service.FoodService.FoodProductService;
import com.example.YumDash.Service.OrderService;
import com.example.YumDash.Service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    private final CartService cartService;
    private final FoodProductRepo foodProductRepo;
    private final UserService userService;
    private final OrderService orderService;
    private final FoodProductService foodProductService;

    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session) throws StripeException {
        Map<Integer, Integer> cart = cartService.getCartFromSession(session);
        Map<Integer, FoodProduct> foodProductMap = new HashMap<>();

        for (Integer productId : cart.keySet()) {
            foodProductMap.put(productId, foodProductRepo.findById(productId).orElse(null));
        }
        model.addAttribute("checkoutDTO", new CheckoutDto());

        return "payment";
    }

    @PostMapping("/checkout")
    public String processCheckout(
            @ModelAttribute CheckoutDto checkoutDTO,
            Principal principal,
            HttpSession session) {


        User user;
        if (principal instanceof OAuth2AuthenticationToken oauthToken) {
            String email = oauthToken.getPrincipal().getAttribute("email");
            user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User OAuth2 nu a fost găsit"));
        } else if (principal != null) {
            String email = principal.getName();
            user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User cu autentificare clasică nu a fost găsit"));
        } else {
            user = new User();
            user.setEmail("anonim" + System.currentTimeMillis() + "@example.com");
            user.setPassword("temporaryPassword123");
            userService.createUser(user);
        }


        String fullAddress = checkoutDTO.getAddress() + ", Block: " + checkoutDTO.getBlock() +
                ", Staircase: " + checkoutDTO.getStaircase() + ", Apartment: " + checkoutDTO.getApartment();


        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }


        Map<Integer, FoodProduct> foodProductMap = foodProductService.getAllFoodProducts();
        double subtotal = cartService.calculateTotal(session, foodProductMap);

        double deliveryFee = subtotal >= 100.0 ? 0.0 : 9.99;
        double packagingFee = 2.50;
        double serviceFee = 1.20;
        double totalAmount = subtotal + deliveryFee + packagingFee + serviceFee;


        UserOrder order = new UserOrder();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now().toString());
        order.setAddress(fullAddress);
        order.setPaymentMethod(checkoutDTO.getPaymentMethod());
        order.setDeliveryMethod(checkoutDTO.getDeliveryMethod());
        order.setComment(checkoutDTO.getComment());
        order.setStatus(checkoutDTO.getStatus());
        order.setAmount(totalAmount);


        List<OrderItem> orderItems = new ArrayList<>();
        for (Integer productId : cart.keySet()) {
            FoodProduct foodProduct = foodProductRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Produsul nu a fost găsit"));

            int quantity = cart.get(productId);

            OrderItem item = new OrderItem();
            item.setFoodProduct(foodProduct);
            item.setQuantity(quantity);
            item.setOrder(order);

            orderItems.add(item);
        }

        order.setItems(orderItems);


        FoodProduct firstProduct = null;
        for (Integer productId : cart.keySet()) {
            firstProduct = foodProductRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Produsul nu a fost găsit"));
            if (firstProduct != null) break;
        }
        if (firstProduct != null) {
            order.setFoodProvider(firstProduct.getFoodProvider());
        }


        session.setAttribute("order", order);


        String paymentMethod = checkoutDTO.getPaymentMethod();
        if ("Cash".equalsIgnoreCase(paymentMethod) || "Card on Delivery".equalsIgnoreCase(paymentMethod)) {
            orderService.createOrder(order);
            return "redirect:/payment/success";
        } else if ("online".equalsIgnoreCase(paymentMethod)) {
            return "redirect:/online";
        } else {
            return "redirect:/checkout?error=metoda_necunoscuta";
        }
    }



    @GetMapping("/payment/cancel")
    public String paymentCancel() {
        return "paymentCancel";
    }

    @GetMapping("/payment/success")
    public String paymentSuccess(HttpSession session, Model model) {
        UserOrder order = (UserOrder) session.getAttribute("order");
        if (order == null) {
            return "redirect:/payment/cancel";
        }
        model.addAttribute("order", order);
        session.removeAttribute("order");
        session.removeAttribute("cart");
        return "paymentSuccess";
    }


    @GetMapping("/online")
    public String showOnlinePaymentPage(HttpSession session, Model model) {
        UserOrder order = (UserOrder) session.getAttribute("order");
        if (order == null) {
            return "redirect:/payment/cancel";
        }

        com.stripe.Stripe.apiKey = stripeSecretKey;

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (order.getAmount() * 100))
                .setCurrency("ron")
                .build();

        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            model.addAttribute("clientSecret", paymentIntent.getClientSecret());
            model.addAttribute("total", order.getAmount());
        } catch (StripeException e) {
            e.printStackTrace();
            return "redirect:/payment/cancel";
        }

        return "payment";
    }

    @PostMapping("/online")
    public String processOnlinePayment(
            @RequestParam String paymentIntentId,
            HttpSession session, Model model) {


        UserOrder order = (UserOrder) session.getAttribute("order");
        if (order == null) {
            return "redirect:/payment/cancel";
        }


        com.stripe.Stripe.apiKey = stripeSecretKey;

        try {

            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);


            if ("succeeded".equals(paymentIntent.getStatus())) {

                order.setStatus("Comanda trimisa");

                @SuppressWarnings("unchecked")
                Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");

                FoodProduct firstProduct = null;
                for (Integer productId : cart.keySet()) {
                    firstProduct = foodProductRepo.findById(productId).orElseThrow(() -> new RuntimeException("Produsul nu a fost gasit"));
                    if (firstProduct != null) break;
                }
                if (firstProduct != null) {
                    order.setFoodProvider(firstProduct.getFoodProvider());
                }

                orderService.createOrder(order);

                model.addAttribute("order", order);
                session.removeAttribute("order");
                session.removeAttribute("cart");
                return "paymentSuccess";
            } else {

                return "redirect:/payment/cancel";
            }
        } catch (StripeException e) {
            e.printStackTrace();
            return "redirect:/payment/cancel";
        }
    }

}
