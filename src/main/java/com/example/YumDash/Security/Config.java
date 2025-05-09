package com.example.YumDash.Security;

import com.example.YumDash.Service.SecurityService.CustomOAuth2AuthenticationSuccessHandler;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class Config {

    private final CustomOAuth2AuthenticationSuccessHandler successHandler;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(c -> c.disable())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/loginForm")
                        .defaultSuccessUrl("/getFoodPage", true)
                        .failureUrl("/loginForm?error=true")
                        .successHandler(successHandler)
                )
                .formLogin(form -> form
                        .loginPage("/loginForm")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler())
                        .failureUrl("/loginForm?error=true")
                        .permitAll()
                ).logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/getFoodPage")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated());
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            var roles = authentication.getAuthorities();

            if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_FOOD_PROVIDER"))) {
                response.sendRedirect("/provider/products");
            } else if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_USER"))) {
                response.sendRedirect("/getFoodPage");
            } else if (roles.stream().anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"))) {
                    response.sendRedirect("/admin/dashboard");
            } else {
                response.sendRedirect("/default");
            }
        };
    }

}
