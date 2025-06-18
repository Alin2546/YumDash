package com.example.YumDash.Service.FoodService;

import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.Food.Review;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.FoodProviderRepo;
import com.example.YumDash.Repository.OrderRepo;
import com.example.YumDash.Repository.ReviewRepository;
import com.example.YumDash.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FoodProviderRepo foodProviderRepo;
    private final UserRepo userRepo;
    private final OrderRepo orderRepo;


    public void saveReview(int userId, int providerId, int orderId, Integer rating, String comment)  {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        FoodProvider provider = foodProviderRepo.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
        UserOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));


        Optional<Review> existingReview = reviewRepository.findByUserAndFoodProviderAndOrder(user, provider, order);

        if (existingReview.isPresent()) {
            Review review = existingReview.get();
            review.setRating(rating);
            review.setComment(comment);
            review.setCreatedAt(LocalDateTime.now());
            reviewRepository.save(review);
        } else {
            Review review = new Review();
            review.setUser(user);
            review.setFoodProvider(provider);
            review.setOrder(order);
            review.setRating(rating);
            review.setComment(comment);
            review.setCreatedAt(LocalDateTime.now());
            reviewRepository.save(review);
        }
        if (!order.isReviewed()) {
            order.setReviewed(true);
            orderRepo.save(order);
        }
    }


    public Double getAverageRating(int providerId) {
        List<Review> reviews = reviewRepository.findByFoodProviderId(providerId);
        return reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }


    public List<Review> getReviewsForProvider(int providerId) {
        return reviewRepository.findAllByFoodProviderId(providerId);
    }
}
