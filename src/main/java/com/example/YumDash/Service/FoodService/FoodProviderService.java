package com.example.YumDash.Service.FoodService;

import com.example.YumDash.Model.Dto.CreateFoodProviderDto;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;

import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Repository.FoodProviderRepo;

import com.example.YumDash.Repository.OrderRepo;
import com.example.YumDash.Repository.UserRepo;
import com.example.YumDash.Service.GoogleMapsService;

import com.example.YumDash.Service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodProviderService {

    private final FoodProviderRepo foodProviderRepo;
    private final GoogleMapsService googleMapsService;
    private final FoodProductRepo foodProductRepo;
    private final UserRepo userRepo;
    private final OrderRepo orderRepo;
    private final UserService userService;


    public FoodProvider getFoodProviderByEmail(String email) {
        return foodProviderRepo.findByEmail(email).orElse(null);
    }

    public List<FoodProvider> searchFoodProviders(String keyword) {
        return foodProviderRepo.findByNameContainingIgnoreCase(keyword);
    }

    public FoodProvider findById(int id) {
        return foodProviderRepo.findById(id).orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public void updateImageUrlByEmail(String email, String newImageUrl) {
        FoodProvider provider = foodProviderRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Provider not found"));
        provider.setImageurl(newImageUrl);
        foodProviderRepo.save(provider);
    }

    public FoodProvider getFoodProviderById(Integer id) {
        Optional<FoodProvider> foodProviderOpt = foodProviderRepo.findById(id);
        return foodProviderOpt.orElse(null);
    }

    public List<FoodProvider> getProvidersByUserActiveStatus(boolean isActive) {
        return foodProviderRepo.findByUserIsActive(isActive);
    }

    @Transactional
    public void deleteProviderById(int id) {
        FoodProvider provider = foodProviderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("FoodProvider not found"));

        User user = provider.getUser();

        List<UserOrder> userOrders = orderRepo.findByFoodProvider(provider);
        for (UserOrder userOrder : userOrders) {
            userOrder.setFoodProvider(null);
            orderRepo.save(userOrder);
        }

        List<FoodProduct> foodProducts = foodProductRepo.findByFoodProvider(provider);
        foodProductRepo.deleteAll(foodProducts);

        foodProviderRepo.delete(provider);

        if (user != null) {
            userRepo.delete(user);
        }
    }

    public List<FoodProvider> getNearbyRestaurants(String userAddress) {

        if (userAddress == null) return null;
        double[] userCoords = googleMapsService.getCoordinates(userAddress);
        if (userCoords == null) return null;
        List<FoodProvider> allRestaurants = foodProviderRepo.findAll();

        return allRestaurants.stream()
                .filter(restaurant -> {
                    double distance = haversine(userCoords[0], userCoords[1], restaurant.getLatitude(), restaurant.getLongitude());
                    return distance <= 5.0;
                })
                .collect(Collectors.toList());
    }

    public boolean isWithinDeliveryRange(FoodProvider provider, String userAddress) {
        if (provider == null || userAddress == null) {
            return false;
        }
        double[] userCoords = googleMapsService.getCoordinates(userAddress);
        if (userCoords == null) return false;
        double distance = haversine(userCoords[0], userCoords[1], provider.getLatitude(), provider.getLongitude());
        return distance <= 5.0;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public FoodProvider getFoodProviderByProductId(Integer productId) {
        Optional<FoodProduct> product = foodProductRepo.findById(productId);
        return product.map(FoodProduct::getFoodProvider).orElse(null);
    }

    public void registerFoodProvider(CreateFoodProviderDto dto) {
        if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("Emailul este deja folosit.");
        }

        double[] coords = googleMapsService.getCoordinates(dto.getAddress());

        FoodProvider provider = dto.mapToFoodProvider();
        if (coords != null) {
            provider.setLatitude(coords[0]);
            provider.setLongitude(coords[1]);
        } else {
            provider.setLatitude(0);
            provider.setLongitude(0);
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole("ROLE_FOOD_PROVIDER");
        user.setAddress(dto.getAddress());
        user.setAuthProviders(new HashSet<>(List.of("YumDash")));


        userService.createUser(user);
        provider.setUser(user);
        foodProviderRepo.save(provider);
    }

    public boolean updateProduct(int id, FoodProduct updatedProduct) {
        Optional<FoodProduct> existingProductOpt = foodProductRepo.findById(id);
        if (existingProductOpt.isPresent()) {
            FoodProduct existingProduct = existingProductOpt.get();
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setImageurl(updatedProduct.getImageurl());
            existingProduct.setCategory(updatedProduct.getCategory());
            foodProductRepo.save(existingProduct);
            return true;
        }
        return false;
    }
}
