package com.example.YumDash.Model.Dto;

import com.example.YumDash.Model.User.User;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Pattern;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class UserCreateDto {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Campul trebuie completat")
    private String name;

    @NotBlank
    @Email
    private String email;


    @Size(min = 8, max = 50, message = "Parola incorecta: Minim 8 caractere necesare")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,50}$", message = "Parola trebuie sa contina cel putin o majuscula si o cifra")
    private String password;


    private String verifypassword;

    private String role = "ROLE_USER";
    private String provider = "yumdash";

    public User mapToUser() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setRole(this.role);
        return user;
    }
}

