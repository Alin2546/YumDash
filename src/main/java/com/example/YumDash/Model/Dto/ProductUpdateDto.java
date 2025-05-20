package com.example.YumDash.Model.Dto;

import com.example.YumDash.Model.Category;
import com.example.YumDash.Model.Food.FoodProduct;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateDto {
    @NotBlank(message = "Numele este obligatoriu.")
    @Size(max = 100, message = "Numele nu poate avea mai mult de 100 de caractere.")
    private String name;

    @NotNull(message = "Prețul este obligatoriu.")
    @DecimalMin(value = "0.01", message = "Prețul trebuie să fie mai mare ca 0.")
    private Double price;

    @NotBlank(message = "URL-ul imaginii este obligatoriu.")
    @Size(max = 255, message = "URL-ul este prea lung.")
    private String imageurl;

    @NotNull(message = "Categoria este obligatorie.")
    private Category category;


    public FoodProduct mapToFoodProduct(FoodProduct existingProduct) {
        existingProduct.setName(this.name);
        existingProduct.setPrice(this.price);
        existingProduct.setImageurl(this.imageurl);
        existingProduct.setCategory(this.category);
        return existingProduct;
    }
}
