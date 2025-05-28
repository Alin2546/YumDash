package com.example.YumDash.Controller;

import com.example.YumDash.Model.Category;
import com.example.YumDash.Model.Dto.CreateFoodProviderDto;
import com.example.YumDash.Model.Dto.ProductCreateDto;
import com.example.YumDash.Model.Dto.ProductUpdateDto;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.*;
import com.example.YumDash.Service.FoodService.EmailAlreadyUsedException;
import com.example.YumDash.Service.FoodService.FoodProductService;
import com.example.YumDash.Service.FoodService.FoodProviderService;
import com.example.YumDash.Service.GoogleMapsService;
import com.example.YumDash.Service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
public class FoodOwnerController {
    private final GoogleMapsService googleMapsService;
    private final FoodProviderService foodProviderService;
    private final FoodProductRepo foodProductRepo;
    private final OrderRepo orderRepo;
    private final OrderService orderService;
    private final FoodProductService foodProductService;


    @GetMapping("/addProvider")
    public String showAddForm(Model model, @RequestParam(value = "status", required = false) String status) {
        if ("success".equals(status)) {
            model.addAttribute("successMessage", "Restaurantul a fost adăugat cu succes!");
        } else if ("error".equals(status)) {
            model.addAttribute("errorMessage", "A apărut o eroare. Vă rugăm să încercați din nou.");
        }
        model.addAttribute("createFoodProviderDto", new CreateFoodProviderDto());
        return "addProvider";
    }


    @PostMapping("/saveProvider")
    public String saveProvider(@ModelAttribute("createFoodProviderDto") @Valid CreateFoodProviderDto createFoodProviderDto,
                               BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            return "addProvider";
        }
        if (!createFoodProviderDto.getPassword().equals(createFoodProviderDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Parola și confirmarea nu coincid");
            return "addProvider";
        }
        try {
            foodProviderService.registerFoodProvider(createFoodProviderDto);
            return "redirect:/successPage";
        } catch (EmailAlreadyUsedException e) {
            model.addAttribute("error", e.getMessage());
            return "addProvider";
        }
    }


    @GetMapping("/successPage")
    public String showSuccessPage() {
        return "successPage";
    }

    @GetMapping("/provider/products")
    public String showProviderProductPage(Model model, Principal principal) {
        String email = principal.getName();
        FoodProvider foodProvider = foodProviderService.getFoodProviderByEmail(email);

        if (foodProvider != null) {
            model.addAttribute("foodProvider", foodProvider);
            model.addAttribute("foodProducts", foodProvider.getFoodProducts());
            model.addAttribute("categories", Category.values());
            model.addAttribute("productCreateDto", new ProductCreateDto());
            List<UserOrder> orders = orderRepo.findByFoodProvider(foodProvider);
            model.addAttribute("orders", orders);
        } else {
            model.addAttribute("error", "Food provider not found.");
        }
        return "providerView";
    }

    @PostMapping("/product/{id}/toggleAvailability")
    public String toggleProductAvailability(@PathVariable("id") int productId) {
        Optional<FoodProduct> optionalProduct = foodProductRepo.findById(productId);
        if (optionalProduct.isPresent()) {
            FoodProduct product = optionalProduct.get();
            product.setAvailable(!product.isAvailable());
            foodProductRepo.save(product);
        }
        return "redirect:/provider/products";
    }

    @GetMapping("/foodOwner/chooseAddress")
    @ResponseBody
    public List<String> getAddressSuggestions(@RequestParam String address) {
        if (address != null && !address.isEmpty()) {
            return googleMapsService.getFormattedAddress(address);
        }
        return Collections.emptyList();
    }


    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute @Valid ProductCreateDto dto, BindingResult bindingResult, Principal principal,Model model) {
        String email = principal.getName();
        FoodProvider provider = foodProviderService.getFoodProviderByEmail(email);

        if (bindingResult.hasErrors()) {
            model.addAttribute("foodProvider", provider);
            model.addAttribute("foodProducts", provider.getFoodProducts());
            model.addAttribute("categories", Category.values());
            model.addAttribute("productCreateDto", dto);
            List<UserOrder> orders = orderRepo.findByFoodProvider(provider);
            model.addAttribute("orders", orders);
            return "providerView";
        }

        if (provider == null) {
            return "providerNotFound";
        }

        FoodProduct product = dto.mapToFoodProduct(provider);
        foodProductRepo.save(product);
        return "redirect:/provider/products";
    }

    @PostMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        foodProductRepo.deleteById(id);
        return "redirect:/provider/products";
    }

    @GetMapping("/editProduct/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {
        Optional<FoodProduct> productOptional = foodProductRepo.findById(id);
        if (productOptional.isPresent()) {
            ProductUpdateDto dto = foodProductService.toDto(productOptional.get());
            model.addAttribute("product", dto);
            model.addAttribute("productId", id);
            return "editProduct";
        } else {
            return "redirect:/provider/products";
        }
    }

    @PostMapping("/editProduct/{id}")
    public String updateProduct(@PathVariable("id") int id,
                                @Valid @ModelAttribute("product") ProductUpdateDto dto,
                                BindingResult result,
                                Model model) {

        if (result.hasErrors()) {
            model.addAttribute("product", dto);
            model.addAttribute("productId", id);
            return "editProduct";
        }

        Optional<FoodProduct> productOptional = foodProductRepo.findById(id);
        if (productOptional.isEmpty()) {
            return "redirect:/provider/products?error=notfound";
        }

        FoodProduct existingProduct = productOptional.get();
        dto.mapToFoodProduct(existingProduct);

        boolean updated = foodProviderService.updateProduct(id, existingProduct);
        if (!updated) {
            return "redirect:/provider/products?error=notfound";
        }
        return "redirect:/provider/products";
    }

    @PostMapping("/updateRestaurantImage")
    public String updateRestaurantImage(@RequestParam("newImageUrl") String newImageUrl, Principal principal) {
        foodProviderService.updateImageUrlByEmail(principal.getName(), newImageUrl);
        return "redirect:/provider/products";
    }


    @PostMapping("/provider/orders/{id}/accept")
    public String acceptOrder(@PathVariable int id) {
        orderService.acceptOrder(id);
        return "redirect:/provider/products";
    }

    @PostMapping("/provider/orders/{id}/cancel")
    public String cancelOrder(@PathVariable int id) {
        orderService.cancelOrder(id);
        return "redirect:/provider/products";
    }

    @PostMapping("/provider/orders/{id}/markDelivered")
    public String markOrderAsDelivered(@PathVariable int id) {
        Optional<UserOrder> optionalOrder = orderRepo.findById(id);
        if (optionalOrder.isPresent()) {
            UserOrder order = optionalOrder.get();
            if ("CONFIRMATA".equals(order.getStatus())) {
                order.setStatus("LIVRATA");
                orderRepo.save(order);
            }
        }
        return "redirect:/provider/products";
    }

}
