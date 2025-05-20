package com.example.YumDash.Controller;

import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.UserRepo;
import com.example.YumDash.Service.FoodService.FoodProviderService;
import com.example.YumDash.Service.OrderService;
import com.example.YumDash.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;


@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final FoodProviderService foodProviderService;
    private final OrderService orderService;
    private final UserRepo userRepo;


    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("usersActive", userService.getUsersByActiveStatus(true));
        model.addAttribute("usersInactive", userService.getUsersByActiveStatus(false));
        model.addAttribute("providersActive", foodProviderService.getProvidersByUserActiveStatus(true));
        model.addAttribute("providersInactive", foodProviderService.getProvidersByUserActiveStatus(false));
        model.addAttribute("orders", orderService.getAllOrders());
        return "adminDashboard";
    }

    @GetMapping("/users/active")
    public String getUsersActive(Model model) {
        List<User> usersActive = userService.getUsersByActiveStatus(true);
        model.addAttribute("usersActive", usersActive);
        return "activeUsers";
    }

    @GetMapping("/users/inactive")
    public String getUsersInactive(Model model) {
        List<User> usersInactive = userService.getUsersByActiveStatus(false);
        model.addAttribute("usersInactive", usersInactive);
        return "inactiveUsers";
    }

    @GetMapping("/providers/active")
    public String getProvidersActive(Model model) {
        List<FoodProvider> providersActive = foodProviderService.getProvidersByUserActiveStatus(true);
        model.addAttribute("providersActive", providersActive);
        return "activeFoodProvider";
    }

    @GetMapping("/providers/inactive")
    public String getProvidersInactive(Model model) {
        List<FoodProvider> providersInactive = foodProviderService.getProvidersByUserActiveStatus(false);
        model.addAttribute("providersInactive", providersInactive);
        return "inactiveFoodProvider";
    }

    @GetMapping("/orders")
    public String filterOrders(@RequestParam(required = false) String clientEmail,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                               Model model) {
        model.addAttribute("orders", orderService.getOrdersByFilters(clientEmail, status, date));
        model.addAttribute("clientEmail", clientEmail);
        model.addAttribute("status", status);
        model.addAttribute("date", date);
        return "adminOrders";
    }

    @PostMapping("/updateOrderStatus")
    public String updateOrderStatus(@RequestParam("orderId") int orderId,
                                    @RequestParam("status") String status,
                                    @RequestHeader(value = "Referer", required = false) final String referer,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateOrderStatus(orderId, status);
            redirectAttributes.addFlashAttribute("successMessage", "Statusul comenzii a fost actualizat cu succes.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Eroare la actualizarea comenzii.");
        }
        return "redirect:" + (referer != null ? referer : "/admin/orders");
    }

    @PostMapping("/updateUserStatus")
    public String updateUserStatus(@RequestParam("id") int id,
                                   @RequestParam("isActive") boolean isActive,
                                   @RequestHeader(value = "Referer", required = false) final String referer,
                                   RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserStatus(id, isActive);
            redirectAttributes.addFlashAttribute("successMessage", "Statusul utilizatorului a fost actualizat.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Eroare la actualizarea utilizatorului.");
        }
        return "redirect:" + (referer != null ? referer : "/admin/dashboard");
    }

    @PostMapping("/updateProviderStatus")
    public String updateProviderStatus(@RequestParam("id") int providerId,
                                       @RequestParam("isActive") boolean isActive,
                                       @RequestHeader(value = "Referer", required = false) final String referer,
                                       RedirectAttributes redirectAttributes) {
        try {
            FoodProvider provider = foodProviderService.findById(providerId);
            if (provider != null && provider.getUser() != null) {
                userService.updateUserStatus(provider.getUser().getId(), isActive);
                redirectAttributes.addFlashAttribute("successMessage", "Statusul furnizorului a fost actualizat cu succes.");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Furnizorul nu a fost găsit.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Eroare la actualizarea furnizorului.");
        }
        return "redirect:" + (referer != null ? referer : "/admin/dashboard");
    }

    @PostMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable int id,
                             @RequestHeader(value = "Referer", required = false) final String referer,
                             RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUserById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Utilizatorul a fost șters cu succes.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Eroare la ștergerea utilizatorului.");
        }
        return "redirect:" + (referer != null ? referer : "/admin/dashboard");
    }

    @PostMapping("/deleteProvider/{id}")
    public String deleteProvider(@PathVariable int id,
                                 @RequestHeader(value = "Referer", required = false) final String referer,
                                 RedirectAttributes redirectAttributes) {
        try {
            foodProviderService.deleteProviderById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Furnizorul a fost șters cu succes.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Eroare la ștergerea furnizorului.");
        }
        return "redirect:" + (referer != null ? referer : "/admin/dashboard");
    }

}
