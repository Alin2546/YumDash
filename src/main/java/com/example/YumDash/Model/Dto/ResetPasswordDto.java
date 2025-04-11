package com.example.YumDash.Model.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDto {
    private String email;
    @Size(min = 8, max = 50, message = "Parola incorecta: Minim 8 caractere necesare")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,50}$", message = "Parola trebuie sa contina cel putin o majuscula si o cifra")
    private String newPassword;


    private String confirmPassword;
}
