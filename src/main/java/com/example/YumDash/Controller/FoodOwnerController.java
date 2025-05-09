package com.example.YumDash.Controller;

import com.example.YumDash.Model.Category;
import com.example.YumDash.Model.Dto.CreateFoodProviderDto;
import com.example.YumDash.Model.Dto.ProductCreateDto;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Repository.FoodProviderRepo;
import com.example.YumDash.Repository.UserRepo;
import com.example.YumDash.Service.FoodService.FoodProviderService;
import com.example.YumDash.Service.GoogleMapsService;
import com.example.YumDash.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
public class FoodOwnerController {
    private final FoodProviderRepo foodProviderRepo;
    private final UserService userService;
    private final UserRepo userRepo;
    private final GoogleMapsService googleMapsService;
    private final FoodProviderService foodProviderService;
    private final FoodProductRepo foodProductRepo;

    @GetMapping("/addProvider")
    public String showAddForm(Model model, @RequestParam(value = "status", required = false) String status) {
        if ("success".equals(status)) {
            model.addAttribute("successMessage", "Restaurantul a fost adăugat cu succes!");
        } else if ("error".equals(status)) {
            model.addAttribute("errorMessage", "A apărut o eroare. Vă rugăm să încercați din nou.");
        }
        model.addAttribute("foodProviderDto", new CreateFoodProviderDto());
        return "addProvider";
    }


    @PostMapping("/saveProvider")
    public String saveProvider(@ModelAttribute CreateFoodProviderDto createFoodProviderDto, Model model) {

        if (userRepo.findByEmail(createFoodProviderDto.getEmail()).isPresent()) {
            model.addAttribute("error", "Emailul este deja folosit.");
            return "addProvider";
        }


        double[] coords = googleMapsService.getCoordinates(createFoodProviderDto.getAddress());

        FoodProvider foodProvider = new FoodProvider();
        foodProvider.setName(createFoodProviderDto.getName());
        foodProvider.setAddress(createFoodProviderDto.getAddress());
        foodProvider.setImageurl(createFoodProviderDto.getImageurl());
        foodProvider.setEmail(createFoodProviderDto.getEmail());


        if (coords != null) {
            foodProvider.setLatitude(coords[0]);
            foodProvider.setLongitude(coords[1]);
        } else {
            foodProvider.setLatitude(0);
            foodProvider.setLongitude(0);
        }


        foodProviderRepo.save(foodProvider);

        User newUser = new User();
        newUser.setEmail(createFoodProviderDto.getEmail());
        newUser.setPassword(createFoodProviderDto.getPassword());
        newUser.setRole("ROLE_FOOD_PROVIDER");
        newUser.setAddress(createFoodProviderDto.getAddress());
        newUser.setProvider(createFoodProviderDto.getName());

        foodProvider.setUser(newUser);

        newUser.setFoodProvider(foodProvider);


        userService.createUser(newUser);

        return "redirect:/successPage";
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
        } else {
            model.addAttribute("error", "Food provider not found.");
        }

        return "providerView";
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
    public String saveProduct(@ModelAttribute ProductCreateDto dto, Principal principal) {
        String email = principal.getName();
        FoodProvider provider = foodProviderService.getFoodProviderByEmail(email);

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
            FoodProduct product = productOptional.get();
            model.addAttribute("product", product);
            return "editProduct";
        } else {
            return "redirect:/provider/products";
        }
    }

    @PostMapping("/editProduct/{id}")
    public String updateProduct(@PathVariable("id") int id,
                                @ModelAttribute("product") FoodProduct updatedProduct) {
        Optional<FoodProduct> existingProductOpt = foodProductRepo.findById(id);

        if (existingProductOpt.isPresent()) {
            FoodProduct existingProduct = existingProductOpt.get();
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setImageurl(updatedProduct.getImageurl());
            existingProduct.setCategory(updatedProduct.getCategory());
            foodProductRepo.save(existingProduct);
        }

        return "redirect:/provider/products";
    }

    @PostMapping("/updateRestaurantImage")
    public String updateRestaurantImage(@RequestParam("newImageUrl") String newImageUrl, Principal principal) {
        foodProviderService.updateImageUrlByEmail(principal.getName(), newImageUrl);
        return "redirect:/provider/products";
    }



}
