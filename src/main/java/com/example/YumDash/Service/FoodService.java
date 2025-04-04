package com.example.YumDash.Service;

import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Repository.FoodProviderRepo;
import com.example.YumDash.Repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodProviderRepo foodProviderRepo;
    private final FoodProductRepo foodProductRepo;
    private final GoogleMapsService googleMapsService;

    public List<FoodProvider> getAllProviders() {
        return foodProviderRepo.findAll((Sort.by(Sort.Direction.ASC, "id")));
    }

    public List<FoodProvider> searchFoodProviders(String keyword) {
        return foodProviderRepo.findByNameContainingIgnoreCase(keyword);
    }

    public FoodProvider findById(int id) {
        return foodProviderRepo.findById(id).orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public List<FoodProduct> getProductsByProviderId(int providerId) {
        return foodProductRepo.findByFoodProviderId(providerId);
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
}
