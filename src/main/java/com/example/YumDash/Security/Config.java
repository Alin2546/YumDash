package com.example.YumDash.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class Config{
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(c -> c.disable())
                .formLogin(form -> form
                        .loginPage("/loginForm")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/foodPage", true)
                        .permitAll()
                )
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated());
        return httpSecurity.build();
    };

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
