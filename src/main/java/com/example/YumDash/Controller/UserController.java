package com.example.YumDash.Controller;

import com.example.YumDash.Model.Dto.UserCreateDto;
import com.example.YumDash.Model.Dto.UserLoginDto;
import com.example.YumDash.Model.FoodProvider;
import com.example.YumDash.Model.Order;
import com.example.YumDash.Model.User;
import com.example.YumDash.Repository.OrderRepo;
import com.example.YumDash.Service.FoodProviderService;
import com.example.YumDash.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FoodProviderService foodProviderService;

    @GetMapping("/register/form")
    public String displayUserForm(Model model) {
        model.addAttribute("UserCreateDto", new UserCreateDto());
        return "createUser";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute UserCreateDto userCreateDto) {
        userService.createUser(userCreateDto.mapToCustomer());
        return "redirect:/submit/address";
    }


    @GetMapping("/login/form")
    public String displayLoginForm(Model model) {
        model.addAttribute("userLoginDto", new UserLoginDto());
        return "loginForm";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute UserLoginDto userLoginDto, Model model) {
        if (userService.authenticateUser(userLoginDto)) {
            model.addAttribute("loggedInUser", userLoginDto);
            List<FoodProvider> foodProviders = foodProviderService.getAllProviders();
            model.addAttribute("foodProviders", foodProviders);
            return "foodPage";
        }
        model.addAttribute("errorMessage", "Invalid email or password");
        return "redirect:/login/form?error=true";
    }

    @GetMapping("/profile")
    public String getUserProfile() {
        return "userProfile";
    }

    @GetMapping("/orders")
    public String getOrders(Model model) {
        List<Order> orderList = userService.getOrders();
        model.addAttribute("orderList", orderList);
        return "userOrders";
    }

    @GetMapping("/orderForm")
    public String orderForm(Model model) {
        model.addAttribute("Order", new Order());
        return "OrderForm";
    }

    @PostMapping("/createOrder")
    public String createOrder(@ModelAttribute Order order) {
        userService.createOrder(order);
        return "foodPage";
    }


}
