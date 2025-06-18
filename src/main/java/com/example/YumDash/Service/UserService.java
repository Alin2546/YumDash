package com.example.YumDash.Service;

import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.Food.Review;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo usersRepo;
    private final PasswordEncoder passwordEncoder;
    private final FoodProviderRepo foodProviderRepo;
    private final FoodProductRepo foodProductRepo;
    private final OrderRepo orderRepo;
    private final ReviewRepository reviewRepo;

    public void createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        usersRepo.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return usersRepo.findByEmail(email);
    }

    public void resetPassword(String email, String newPassword) {
        User user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizatorul cu acest email nu a fost găsit"));

        user.setPassword(passwordEncoder.encode(newPassword));
        usersRepo.save(user);
    }
    @Transactional
    public void deleteUserById(int id) {
        Optional<User> optionalUser = usersRepo.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        User user = optionalUser.get();

        List<Review> userReviews = reviewRepo.findByUser(user);
        if (userReviews != null && !userReviews.isEmpty()) {
            reviewRepo.deleteAll(userReviews);
        }

        List<UserOrder> userOrders = user.getUserOrders();
        if (userOrders != null && !userOrders.isEmpty()) {
            for (UserOrder order : userOrders) {
                List<Review> orderReviews = reviewRepo.findByOrder(order);
                if (orderReviews != null && !orderReviews.isEmpty()) {
                    reviewRepo.deleteAll(orderReviews);
                }
            }
            orderRepo.deleteAll(userOrders);
        }

        FoodProvider foodProvider = foodProviderRepo.findByUser(user);
        if (foodProvider != null) {
            List<Review> providerReviews = reviewRepo.findByFoodProvider(foodProvider);
            if (providerReviews != null && !providerReviews.isEmpty()) {
                reviewRepo.deleteAll(providerReviews);
            }

            List<UserOrder> providerOrders = orderRepo.findByFoodProvider(foodProvider);
            if (providerOrders != null && !providerOrders.isEmpty()) {
                for (UserOrder order : providerOrders) {
                    List<Review> orderReviews = reviewRepo.findByOrder(order);
                    if (orderReviews != null && !orderReviews.isEmpty()) {
                        reviewRepo.deleteAll(orderReviews);
                    }
                }
                orderRepo.deleteAll(providerOrders);
            }

            List<FoodProduct> foodProducts = foodProductRepo.findByFoodProvider(foodProvider);
            if (foodProducts != null && !foodProducts.isEmpty()) {
                foodProductRepo.deleteAll(foodProducts);
            }

            foodProviderRepo.delete(foodProvider);
        }

        usersRepo.delete(user);
    }


    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public void updateUser(User user) {
        usersRepo.save(user);
    }

    public List<User> getUsersByActiveStatus(boolean isActive) {
        return usersRepo.findByIsActive(isActive);
    }

    public void updateUserStatus(int userId, boolean isActive) {
        User user = usersRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setActive(isActive);
        usersRepo.save(user);
    }

}

