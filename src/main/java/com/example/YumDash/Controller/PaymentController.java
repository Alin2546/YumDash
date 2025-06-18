package com.example.YumDash.Controller;

import com.example.YumDash.Model.Dto.CheckoutDto;
import com.example.YumDash.Model.Dto.ProductQuantityDto;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.OrderItem;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Repository.OrderItemRepository;
import com.example.YumDash.Service.FoodService.CartService;
import com.example.YumDash.Service.FoodService.FoodProductService;

import com.example.YumDash.Service.OrderService;
import com.example.YumDash.Service.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.security.Principal;
import java.util.HashMap;
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
    private final OrderItemRepository orderItemRepository;


    @PostMapping("/checkout")
    public String processCheckout(
            @Valid @ModelAttribute CheckoutDto checkoutDto,
            BindingResult bindingResult,
            Principal principal, Model model,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            cartService.populateCartViewModel(model, session, checkoutDto,principal);
            return "cartView";
        }

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

        String fullAddress = checkoutDto.getDeliveryAddress() + ", Block: " + checkoutDto.getBlock() +
                ", Staircase: " + checkoutDto.getStaircase() + ", Apartment: " + checkoutDto.getApartment();

        @SuppressWarnings("unchecked")
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        Map<Integer, FoodProduct> foodProductMap = foodProductService.getAllFoodProducts();
        double subtotal = cartService.calculateTotal(session, foodProductMap);

        boolean eligibleForDiscount = user.isPhoneVerified() && !user.isDiscountUsed();
        double discount = 0.0;

        if (eligibleForDiscount) {
            discount = subtotal * 0.5;
            subtotal -= discount;

            user.setDiscountUsed(true);
            userService.updateUser(user);
        }

        double deliveryFee = subtotal >= 100.0 ? 0.0 : 9.99;
        double packagingFee = 2.50;
        double serviceFee = 1.20;
        double totalAmount = subtotal + deliveryFee + packagingFee + serviceFee;

        UserOrder order = checkoutDto.mapToUserOrder(user, totalAmount);

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
        orderService.createOrder(order);
        for (ProductQuantityDto pq : checkoutDto.getProducts()) {
            FoodProduct product = foodProductRepo.findById(pq.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produsul cu ID " + pq.getProductId() + " nu a fost găsit"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(pq.getQuantity());
            orderItemRepository.save(item);
            order.getOrderItems().add(item);
        }
        String paymentMethod = checkoutDto.getPaymentMethod();
        if ("Cash".equalsIgnoreCase(paymentMethod) || "Card on Delivery".equalsIgnoreCase(paymentMethod)) {
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

    @GetMapping("/order/track/{id}")
    public String trackOrder(@PathVariable int id, Model model) {
        UserOrder order = orderService.findById(id).orElseThrow(() -> new RuntimeException("Comanda nu a fost gasita"));
        if (order == null) {
            return "redirect:/";
        }

        model.addAttribute("order", order);
        return "trackOrder";
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
                order.setStatus("TRIMISA");
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
