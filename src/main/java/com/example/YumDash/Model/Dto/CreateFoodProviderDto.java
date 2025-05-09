package com.example.YumDash.Model.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFoodProviderDto {
    private String name;
    private String address;

    @Email
    private String email;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,50}$", message = "Parola trebuie sa contina cel putin o majuscula si o cifra")
    private String password;

    private String imageurl;
    private Double latitude;
    private Double longitude;
}
