package com.example.YumDash.Model.Dto;

import com.example.YumDash.Model.Food.FoodProvider;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFoodProviderDto {
    @NotBlank(message = "Numele restaurantului este obligatoriu")
    @Size(min = 2, max = 100, message = "Numele trebuie să aibă între 2 și 100 de caractere")
    private String name;

    @NotBlank(message = "Adresa este obligatorie")
    @Size(min = 5, max = 200, message = "Adresa trebuie să aibă între 5 și 200 de caractere")
    private String address;

    @Email(message = "Emailul nu este valid")
    @NotBlank(message = "Emailul este obligatoriu")
    private String email;

    @NotBlank(message = "Parola este obligatorie")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).{8,50}$",
            message = "Parola trebuie să conțină între 8 și 50 de caractere, cel puțin o literă mare și o cifră")
    private String password;

    @NotBlank(message = "Confirmarea parolei este obligatorie")
    private String confirmPassword;

    @NotBlank(message = "URL-ul imaginii este obligatoriu")
    @Size(max = 500, message = "URL-ul imaginii nu poate avea mai mult de 500 de caractere")
    private String imageurl;

    @NotBlank(message = "Numărul de telefon este obligatoriu")
    @Pattern(regexp = "^(\\+4)?07[0-9]{8}$", message = "Număr de telefon invalid. Exemplu: 07XX.XXX.XXX")
    private String phoneNumber;

    private Double latitude;
    private Double longitude;

    public FoodProvider mapToFoodProvider(){
        FoodProvider foodProvider = new FoodProvider();
        foodProvider.setName(this.name);
        foodProvider.setAddress(this.address);
        foodProvider.setEmail(this.email);
        foodProvider.setImageurl(this.imageurl);
        foodProvider.setPhoneNumber(this.phoneNumber);
        return foodProvider;
    }


}
