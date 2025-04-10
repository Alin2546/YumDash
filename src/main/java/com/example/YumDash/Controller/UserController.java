package com.example.YumDash.Controller;

import com.example.YumDash.Model.Dto.UserCreateDto;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Service.SecurityService.MyUser;
import com.example.YumDash.Service.OrderService;
import com.example.YumDash.Service.UserService;
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


import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OrderService orderService;


    @GetMapping("/register/form")
    public String displayUserForm(Model model) {
        model.addAttribute("userCreateDto", new UserCreateDto());
        return "createUser";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid UserCreateDto userCreateDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userCreateDto", userCreateDto);
            return "createUser";
        }
            else if(userService.findByEmail(userCreateDto.getEmail()).isPresent()){
            bindingResult.rejectValue("email", "error.email", "Emailul tau a fost deja inregistrat!");
            return "createUser";
            }

        else if (!(userCreateDto.getPassword().equals(userCreateDto.getVerifypassword()))) {
            bindingResult.rejectValue("verifypassword", "error.verifypassword", "Parolele nu se potrivesc!");
            return "createUser";
        }
        userService.createUser(userCreateDto.mapToUser());
        return "redirect:/loginForm";
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
            } else if (principal instanceof OAuth2User oauth2User) {
                String githubLogin = oauth2User.getAttribute("login");
                String email = oauth2User.getAttribute("email");

                model.addAttribute("authProvider", "Facebook");
                model.addAttribute("userName", githubLogin);
                model.addAttribute("userEmail", email);
            } else if (principal instanceof MyUser myUser) {
                model.addAttribute("authProvider", "Local");
                model.addAttribute("userName", myUser.getName());
                model.addAttribute("userEmail", myUser.getUsername());
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
        model.addAttribute("userOrders", userOrders);
        return "userOrders";
    }



}
