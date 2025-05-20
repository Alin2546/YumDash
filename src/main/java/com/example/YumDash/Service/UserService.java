package com.example.YumDash.Service;

import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.FoodProductRepo;
import com.example.YumDash.Repository.FoodProviderRepo;
import com.example.YumDash.Repository.OrderRepo;
import com.example.YumDash.Repository.UserRepo;
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

    public void createUser(User user) {
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

        List<UserOrder> userOrders = user.getUserOrders();
        if (userOrders != null) {
            orderRepo.deleteAll(userOrders);
        }

        FoodProvider foodProvider = foodProviderRepo.findByUser(user);
        if (foodProvider != null) {
            List<UserOrder> providerOrders = orderRepo.findByFoodProvider(foodProvider);
            orderRepo.deleteAll(providerOrders);
            List<FoodProduct> foodProducts = foodProductRepo.findByFoodProvider(foodProvider);
            foodProductRepo.deleteAll(foodProducts);
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
