package com.example.YumDash.Model.Dto;

import com.example.YumDash.Model.Category;
import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.FoodProvider;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateDto {
    private String name;
    private double price;
    private String imageurl;
    private Category category;

    public FoodProduct mapToFoodProduct(FoodProvider provider) {
        FoodProduct product = new FoodProduct();
        product.setName(this.name);
        product.setPrice(this.price);
        product.setImageurl(this.imageurl);
        product.setCategory(this.category);
        product.setFoodProvider(provider);
        return product;
    }

}
