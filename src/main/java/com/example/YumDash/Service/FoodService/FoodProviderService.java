package com.example.YumDash.Service.FoodService;

import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Repository.FoodProviderRepo;
import com.example.YumDash.Repository.UserRepo;
import com.example.YumDash.Service.GoogleMapsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodProviderService {

    private final FoodProviderRepo foodProviderRepo;
    private final GoogleMapsService googleMapsService;
    private final FoodProductRepo foodProductRepo;
    private final UserRepo userRepo;


    public FoodProvider getFoodProviderByEmail(String email) {
        return foodProviderRepo.findByEmail(email).orElse(null);
    }

    public List<FoodProvider> searchFoodProviders(String keyword) {
        return foodProviderRepo.findByNameContainingIgnoreCase(keyword);
    }

    public FoodProvider findById(int id) {
        return foodProviderRepo.findById(id).orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public List<FoodProvider> getAllProviders() {
        return foodProviderRepo.findAll();
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

    @Transactional
    public void deleteProviderById(int id) {

        FoodProvider provider = foodProviderRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("FoodProvider not found"));


        List<User> users = userRepo.findByFoodProvider(provider);
        for (User user : users) {
            user.setFoodProvider(null);
            userRepo.save(user);
        }


        foodProductRepo.deleteAllByFoodProvider(provider);
        foodProviderRepo.delete(provider);
    }


    public List<FoodProvider> getNearbyRestaurants(String userAddress) {

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
}
