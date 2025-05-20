package com.example.YumDash.Model.Dto;

import com.example.YumDash.Model.Category;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateDto {

    @NotBlank(message = "Numele produsului este obligatoriu.")
    @Size(min = 2, max = 100, message = "Numele produsului trebuie să aibă între 2 și 100 de caractere.")
    private String name;

    @DecimalMin(value = "0.1", message = "Prețul trebuie să fie mai mare decât 0.")
    private double price;

    @NotBlank(message = "URL-ul imaginii este obligatoriu.")
    private String imageurl;

    @NotNull(message = "Categoria este obligatorie.")
    private Category category;

    @NotNull(message = "Disponibilitatea produsului este obligatorie.")
    private boolean available = true;

    public FoodProduct mapToFoodProduct(FoodProvider provider) {
        FoodProduct product = new FoodProduct();
        product.setName(this.name);
        product.setPrice(this.price);
        product.setImageurl(this.imageurl);
        product.setCategory(this.category);
        product.setAvailable(this.available);
        product.setFoodProvider(provider);
        return product;
    }

}
