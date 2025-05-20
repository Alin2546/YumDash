package com.example.YumDash.Service.SecurityService;


import com.example.YumDash.Model.User.User;
import com.example.YumDash.Repository.UserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;


@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepo userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        String currentProvider = capitalize(registrationId);

        String sessionAddress = (String) request.getSession().getAttribute("savedAddress");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setAuthProviders(new HashSet<>(List.of(currentProvider)));
            newUser.setRole("ROLE_USER");

            if (sessionAddress != null && !sessionAddress.isBlank()) {
                newUser.setAddress(sessionAddress);
            }

            return userRepository.save(newUser);
        });


        if (!user.getAuthProviders().contains(currentProvider)) {
            user.getAuthProviders().add(currentProvider);
            userRepository.save(user);
        }


        if (sessionAddress != null && (user.getAddress() == null || user.getAddress().isBlank())) {
            user.setAddress(sessionAddress);
            userRepository.save(user);
        }

        request.getSession().setAttribute("address", user.getAddress());
        response.sendRedirect("/getFoodPage");
    }

    private String capitalize(String input) {
        return input == null || input.isEmpty() ? input : input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }


}
