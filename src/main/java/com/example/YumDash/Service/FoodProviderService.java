package com.example.YumDash.Service;

import com.example.YumDash.Model.FoodProvider;
import com.example.YumDash.Model.Order;
import com.example.YumDash.Repository.FoodProviderRepo;
import com.example.YumDash.Repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodProviderService {

    private final FoodProviderRepo foodProviderRepo;
    private final OrderRepo orderRepo;

    public List<FoodProvider> getAllProviders() {
        return foodProviderRepo.findAll((Sort.by(Sort.Direction.ASC, "id")));
    }

    public List<FoodProvider> searchFoodProviders(String keyword) {
        return foodProviderRepo.findByNameContainingIgnoreCase(keyword);
    }

    public FoodProvider findById(int id) {
        return foodProviderRepo.findById(id).orElseThrow(() -> new RuntimeException("Restaurant not found"));
    }

    public List<FoodProvider> findByCounty(String keyword) {
        return foodProviderRepo.findByCounty(keyword);
    }


}
