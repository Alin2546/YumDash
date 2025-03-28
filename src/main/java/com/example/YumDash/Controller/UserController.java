package com.example.YumDash.Controller;

import com.example.YumDash.Model.Dto.UserCreateDto;
import com.example.YumDash.Model.FoodProvider;
import com.example.YumDash.Model.Order;
import com.example.YumDash.Model.User;
import com.example.YumDash.Service.FoodProviderService;
import com.example.YumDash.Service.MyUser;
import com.example.YumDash.Service.OrderService;
import com.example.YumDash.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
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
    private final OrderService orderService;

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

    @GetMapping("/loginForm")
    public String showLoginPage() {
        return "loginForm";
    }

    @GetMapping("/foodPage")
    public String showFoodPage(Model model, Authentication authentication) {
        if (authentication != null) {
            MyUser myUser = (MyUser) authentication.getPrincipal();
            model.addAttribute("loggedInUser", myUser.getUsername());
        }
        List<FoodProvider> foodProviders = foodProviderService.getAllProviders();
        model.addAttribute("foodProviders", foodProviders);
        return "foodPage";
    }

    @GetMapping("/orders")
    public String getOrders(Model model, Authentication authentication) {
        User user = userService.findByEmail(authentication.getName());
        List<Order> orders = orderService.findOrdersByUserId(user.getId());
        model.addAttribute("orders", orders);

        return "userOrders";
    }

    @GetMapping("/orderForm")
    public String orderForm(Model model) {
        model.addAttribute("Order", new Order());
        return "OrderForm";
    }

//    @PostMapping("/createOrder")
//    public String createOrder(@ModelAttribute Order order) {
//        userService.createOrder(order);
//        return "foodPage";
//    }

    @GetMapping("/profile")
    public String getUserProfile(Model model, Authentication authentication){
        MyUser myUser = (MyUser) authentication.getPrincipal();
        model.addAttribute("user", myUser);
        return "userProfile";
    }

}
