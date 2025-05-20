package com.example.YumDash.Model.Dto;

import com.example.YumDash.Model.User.User;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import jakarta.validation.constraints.*;

import java.util.HashSet;




@Getter
@Setter
public class UserCreateDto {

    @NotBlank(message = "Numele este obligatoriu")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Numele trebuie sa contina doar litere si spatii")
    private String name;

    @NotBlank(message = "Emailul este obligatoriu")
    @Email(message = "Emailul nu este valid")
    private String email;

    @NotBlank(message = "Parola este obligatorie")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,50}$", message = "Parola trebuie sa contina cel putin o majuscula si o cifra")
    private String password;

    @NotBlank(message = "Confirmarea parolei este obligatorie")
    private String verifyPassword;

    private boolean isActive = false;
    private String role = "ROLE_USER";
    private String authProvider = "YumDash";


    private String verificationCode;

    public User mapToUser() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setRole(this.role);
        user.setActive(this.isActive);
        user.setVerificationCode(this.verificationCode);

        if (this.authProvider != null && !this.authProvider.isBlank()) {
            user.setAuthProviders(new HashSet<>(List.of(this.authProvider)));
        } else {
            user.setAuthProviders(new HashSet<>());
        }
        return user;
    }
}