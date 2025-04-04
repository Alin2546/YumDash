package com.example.YumDash.Controller;

import com.example.YumDash.Model.Dto.UserCreateDto;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Service.FoodService;
import com.example.YumDash.Service.SecurityService.MyUser;
import com.example.YumDash.Service.OrderService;
import com.example.YumDash.Service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
        } else if (!(userCreateDto.getPassword().equals(userCreateDto.getVerifypassword()))) {
            bindingResult.rejectValue("verifypassword", "error.verifypassword", "Parolele nu se potrivesc!");
            return "createUser";
        }
        userService.createUser(userCreateDto.mapToUser());
        return "redirect:/submit/address";
    }

    @GetMapping("/loginForm")
    public String showLoginPage() {
        return "loginForm";
    }



    @GetMapping("/profile")
    public String getUserProfile(Model model, Authentication authentication) {
        MyUser myUser = (MyUser) authentication.getPrincipal();
        model.addAttribute("user", myUser);
        return "userProfile";
    }

    @GetMapping("/orders")
    public String getUserOrders(Model model, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        List<UserOrder> userOrders = orderService.findOrdersByUser(user);
        model.addAttribute("userOrders", userOrders);
        return "userOrders";
    }

    @GetMapping("/userShoppingCart")
    public String getUserCart() {
        return "shoppingCart";
    }

}
