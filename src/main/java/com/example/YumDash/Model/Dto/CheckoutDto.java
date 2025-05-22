package com.example.YumDash.Model.Dto;


import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CheckoutDto {

    @NotBlank(message = "Numărul de telefon este obligatoriu")
    @Pattern(regexp = "^(\\+4)?07[0-9]{8}$", message = "Număr de telefon invalid. Exemplu: 07XX.XXX.XXX")
    private String phoneNumber;

    @NotNull(message = "Metoda de plată este obligatorie")
    private String paymentMethod;

    @NotNull(message = "Metoda de livrare este obligatorie")
    private String deliveryMethod;

    @NotBlank(message = "Adresa este obligatorie")
    @Size(min = 5, max = 200, message = "Adresa trebuie să aibă între 5 și 200 de caractere")
    private String deliveryAddress;

    @Size(max = 10, message = "Blocul nu poate depăși 10 caractere")
    private String block;

    @Size(max = 10, message = "Scara nu poate depăși 10 caractere")
    private String staircase;

    @Size(max = 10, message = "Apartamentul nu poate depăși 10 caractere")
    private String apartment;

    @Size(max = 500, message = "Comentariul nu poate depăși 500 de caractere")
    private String comment;

    private String status = "TRIMISA";

    private List<ProductQuantityDto> products = new ArrayList<>();


    public UserOrder mapToUserOrder(User user, double totalAmount) {
        UserOrder userOrder = new UserOrder();
        userOrder.setUser(user);
        userOrder.setOrderDate(LocalDateTime.now());
        userOrder.setAddress(this.deliveryAddress);
        userOrder.setPaymentMethod(this.paymentMethod);
        userOrder.setDeliveryMethod(this.deliveryMethod);
        userOrder.setComment(this.comment);
        userOrder.setStatus(this.status);
        userOrder.setPhoneNumber(this.phoneNumber);
        userOrder.setAmount(totalAmount);
        return userOrder;
    }


}
