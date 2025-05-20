package com.example.YumDash.Model.Dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductQuantityDto {
    private int productId;

    @Min(value = 1, message = "Cantitatea trebuie să fie cel puțin 1")
    private int quantity;

}
