package com.example.YumDash.Controller;

import com.example.YumDash.Service.FoodService.FoodProviderService;
import com.example.YumDash.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final FoodProviderService foodProviderService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("foodProviders", foodProviderService.getAllProviders());
        return "adminDashboard";
    }

    @PostMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/deleteProvider/{id}")
    public String deleteProvider(@PathVariable int id) {
        foodProviderService.deleteProviderById(id);
        return "redirect:/admin/dashboard";
    }
}
