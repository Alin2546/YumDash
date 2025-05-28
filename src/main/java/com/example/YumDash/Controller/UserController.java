package com.example.YumDash.Controller;

import com.example.YumDash.Model.Dto.OrderItemDto;
import com.example.YumDash.Model.Dto.ResetPasswordDto;
import com.example.YumDash.Model.Dto.UserCreateDto;
import com.example.YumDash.Model.Dto.UserOrderViewDto;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.Food.OrderItem;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Service.*;
import com.example.YumDash.Service.SecurityService.MyUser;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OrderService orderService;
    private final EmailService emailService;
    private final PhoneService phoneService;
    private final SmsService smsService;

    @GetMapping("/register/form")
    public String displayUserForm(Model model) {
        model.addAttribute("userCreateDto", new UserCreateDto());
        return "createUser";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid UserCreateDto userCreateDto,
                               BindingResult bindingResult,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("userCreateDto", userCreateDto);
            return "createUser";
        }
        if (userService.findByEmail(userCreateDto.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.email", "Emailul tau a fost deja inregistrat!");
            return "createUser";
        }
        if (!userCreateDto.getPassword().equals(userCreateDto.getVerifyPassword())) {
            bindingResult.rejectValue("verifyPassword", "error.verifyPassword", "Parolele nu se potrivesc!");
            return "createUser";
        }

        String verificationCode = userService.generateVerificationCode();

        userCreateDto.setVerificationCode(verificationCode);
        User user = userCreateDto.mapToUser();

        userService.createUser(user);

        emailService.sendVerificationEmail(user.getEmail(), verificationCode);
        model.addAttribute("email", user.getEmail());
        return "verifyEmail";
    }

    @PostMapping("/verifyCode")
    public String verifyCode(@RequestParam String email,
                             @RequestParam String verificationCode,
                             Model model) {

        Optional<User> optionalUser = userService.findByEmail(email);

        if (optionalUser.isEmpty()) {
            model.addAttribute("error", "Email invalid.");
            return "verifyEmail";
        }

        User user = optionalUser.get();

        if (user.getVerificationCode() != null && user.getVerificationCode().equals(verificationCode)) {
            user.setActive(true);
            user.setVerificationCode(null);
            userService.updateUser(user);

            return "verifyCodeSuccess";
        } else {
            model.addAttribute("email", email);
            model.addAttribute("error", "Codul introdus nu este valid!");
            return "verifyEmail";
        }
    }

    @GetMapping("/loginForm")
    public String showLoginPage() {
        return "loginForm";
    }

    @GetMapping("/profile")
    public String getUserProfile(Model model, Authentication authentication) {
        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof OidcUser oidcUser) {
                String fullName = oidcUser.getFullName();
                String email = oidcUser.getEmail();

                model.addAttribute("authProvider", "Google");
                model.addAttribute("userName", fullName);
                model.addAttribute("userEmail", email);
                User user = userService.findByEmail(email).orElse(null);
                model.addAttribute("user", user);

            } else if (principal instanceof OAuth2User oauth2User) {
                String githubLogin = oauth2User.getAttribute("login");
                String email = oauth2User.getAttribute("email");

                model.addAttribute("authProvider", "Facebook");
                model.addAttribute("userName", githubLogin);
                model.addAttribute("userEmail", email);

                User user = userService.findByEmail(email).orElse(null);
                model.addAttribute("user", user);

            } else if (principal instanceof MyUser myUser) {
                model.addAttribute("authProvider", "Local");
                model.addAttribute("userName", myUser.getName());
                model.addAttribute("userEmail", myUser.getUsername());
                model.addAttribute("user", myUser);
            }
        }
        return "userProfile";
    }

    @GetMapping("/orders")
    public String getUserOrders(Model model, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String email = null;

        if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        } else {
            email = authentication.getName();
        }

        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<UserOrder> userOrders = orderService.findOrdersByUser(user);

        List<UserOrderViewDto> orderDtos = userOrders.stream()
                .filter(order -> order.getFoodProvider() != null)
                .map(order -> {
                    FoodProvider provider = order.getFoodProvider();
                    List<OrderItemDto> items = order.getOrderItems().stream()
                            .map(item -> new OrderItemDto(item.getProduct().getName(), item.getQuantity()))
                            .collect(Collectors.toList());

                    return new UserOrderViewDto(
                            order.getId(),
                            provider.getName(),
                            provider.getImageurl(),
                            order.getOrderDate(),
                            order.getPhoneNumber(),
                            order.getAmount(),
                            order.getStatus(),
                            order.getUser() != null ? order.getUser().getEmail() : null,
                            order.getFoodProvider().getName(),
                            order.getAddress(),
                            order.getPaymentMethod(),
                            order.getDeliveryMethod(),
                            order.getComment(),
                            items
                    );
                })
                .collect(Collectors.toList());
        model.addAttribute("userOrders", orderDtos);
        return "userOrders";
    }

    @GetMapping("/resetPasswordForm")
    public String resetPasswordForm(@RequestParam("email") String email, Model model) {
        model.addAttribute("resetPasswordDto", new ResetPasswordDto());
        model.addAttribute("email", email);
        return "resetPassword";
    }

    @PostMapping("/orders/reorder")
    public String reorder(@RequestParam("orderId") int orderId,
                          Authentication authentication,
                          HttpSession session) {

        UserOrder originalOrder = orderService.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Comanda nu a fost găsită."));

        String email = authentication.getPrincipal() instanceof OAuth2User
                ? (String) ((OAuth2User) authentication.getPrincipal()).getAttributes().get("email")
                : authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit."));

        UserOrder draftOrder = new UserOrder();
        draftOrder.setUser(user);
        draftOrder.setFoodProvider(originalOrder.getFoodProvider());
        draftOrder.setOrderDate(LocalDateTime.now());
        draftOrder.setAmount(originalOrder.getAmount());
        draftOrder.setStatus("Plasată");
        draftOrder.setAddress(originalOrder.getAddress());
        draftOrder.setPaymentMethod(originalOrder.getPaymentMethod());
        draftOrder.setDeliveryMethod(originalOrder.getDeliveryMethod());
        draftOrder.setComment(originalOrder.getComment());
        draftOrder.setPhoneNumber(originalOrder.getPhoneNumber());

        List<OrderItem> newItems = new ArrayList<>();
        Map<Integer, Integer> cart = new HashMap<>();

        for (OrderItem item : originalOrder.getOrderItems()) {
            OrderItem newItem = new OrderItem();
            newItem.setProduct(item.getProduct());
            newItem.setQuantity(item.getQuantity());
            newItem.setOrder(draftOrder);
            newItems.add(newItem);

            cart.put(item.getProduct().getId(), item.getQuantity());
        }
        draftOrder.setOrderItems(newItems);

        session.setAttribute("draftOrder", draftOrder);
        session.setAttribute("cart", cart);

        return "redirect:/cart/view";
    }


    @PostMapping("/resetPassword")
    public String resetPassword(@Valid @ModelAttribute ResetPasswordDto resetPasswordDto,
                                BindingResult result,
                                @RequestParam("email") String email,
                                Model model) {

        if (result.hasErrors()) {
            model.addAttribute("resetPasswordDto", resetPasswordDto);
            return "resetPassword";
        }
        if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.resetPasswordDto", "Parola și confirmarea parolei nu se potrivesc.");
            return "resetPassword";
        }
        try {
            userService.resetPassword(email, resetPasswordDto.getNewPassword());
            emailService.sendPasswordChangedConfirmationEmail(email);
        } catch (RuntimeException e) {
            result.rejectValue("email", "error.resetPasswordDto", e.getMessage());
            return "resetPassword";
        }
        return "redirect:/passwordChangedSuccess";
    }

    @GetMapping("/forgotPassword")
    public String showForgotPasswordPage() {
        return "forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Utilizatorul cu acest email nu a fost găsit.");
            return "forgotPassword";
        }

        String resetLink = "http://localhost:8080/resetPasswordForm?email=" + email;

        try {
            emailService.sendResetPasswordEmail(email, resetLink);
            model.addAttribute("email", email);
            return "emailSentView";
        } catch (Exception e) {
            model.addAttribute("error", "A apărut o eroare la trimiterea emailului.");
            return "forgotPassword";
        }
    }

    @GetMapping("/passwordChangedSuccess")
    public String successPassword() {
        return "resetPasswordSuccess";
    }

    private String extractEmailFromPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser oidcUser) {
            return oidcUser.getEmail();
        } else if (principal instanceof OAuth2User oauth2User) {
            return oauth2User.getAttribute("email");
        } else if (principal instanceof MyUser myUser) {
            return myUser.getUsername();
        } else {

            return authentication.getName();
        }
    }

    @PostMapping("/verifyphone")
    public String sendCode(@RequestParam String phone, Authentication authentication, RedirectAttributes redirectAttributes) {
        String email = extractEmailFromPrincipal(authentication);
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Utilizator inexistent."));

        try {
            phone = smsService.formatPhoneNumber(phone);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("statusMessage", "Număr de telefon invalid.");
            return "redirect:/profile";
        }

        try {
            phoneService.startPhoneVerification(user, phone);
            redirectAttributes.addFlashAttribute("phone", phone);
            redirectAttributes.addFlashAttribute("statusMessage", "Codul a fost trimis prin SMS!");
            return "redirect:/verifyphone?phone=" + phone;
        } catch (com.twilio.exception.ApiException ex) {
            if (ex.getMessage().toLowerCase().contains("unverified")) {
                redirectAttributes.addFlashAttribute("statusMessage", "Numărul pe care vrei să îl trimiți este invalid sau neverificat.");
            } else {
                redirectAttributes.addFlashAttribute("statusMessage", "A apărut o eroare la trimiterea codului. Încearcă din nou.");
            }
            return "redirect:/profile";
        }
    }


    @PostMapping("/confirmphone")
    public String confirmCode(@RequestParam String phone,
                              @RequestParam String code,
                              Authentication authentication,
                              Model model) {

        String email = extractEmailFromPrincipal(authentication);
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Utilizator inexistent."));

        boolean success = phoneService.verifyCode(user, phone, code);

        if (success) {
            user.setPhoneVerified(true);
            userService.updateUser(user);
            model.addAttribute("redirectUrl", "/profile");
            return "phoneVerified";
        }

        model.addAttribute("phone", phone);
        model.addAttribute("errorMessage", "Codul introdus este greșit sau expirat.");
        return "verifyphone";
    }


    @GetMapping("/verifyphone")
    public String showVerificationPage(@RequestParam String phone, Model model) {
        model.addAttribute("phone", phone);
        return "verifyphone";
    }


}


