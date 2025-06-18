package com.example.YumDash.Controller;

import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.Food.Review;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Repository.FoodProviderRepo;
import com.example.YumDash.Service.FoodService.ReviewService;
import com.example.YumDash.Service.SecurityService.MyUser;
import com.example.YumDash.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final FoodProviderRepo foodProviderRepo;
    private final UserService userService;


    @GetMapping("/provider/{providerId}")
    public String showReviews(@PathVariable int providerId, Model model) {
        FoodProvider provider = foodProviderRepo.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Provider not found"));

        List<Review> reviews = reviewService.getReviewsForProvider(providerId);
        Double averageRating = reviewService.getAverageRating(providerId);

        model.addAttribute("provider", provider);
        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("newReview", new Review());

        return "reviews";
    }


    @PostMapping("/provider/{providerId}")
    public String addReview(@PathVariable Long providerId,
                            @RequestParam Integer rating,
                            @RequestParam String comment,
                            @RequestParam int orderId,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        String email = null;
        if (authentication.getPrincipal() instanceof DefaultOidcUser) {
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            email = oidcUser.getEmail();
        } else if (authentication.getPrincipal() instanceof MyUser) {
            email = ((MyUser) authentication.getPrincipal()).getUsername();
        } else {
            throw new RuntimeException("Unknown user principal type");
        }

        User user = userService.findByEmail(email).orElseThrow();
        int userId = user.getId();


        reviewService.saveReview(userId, providerId.intValue(), orderId, rating, comment);
        return "redirect:/orders";
    }

}
