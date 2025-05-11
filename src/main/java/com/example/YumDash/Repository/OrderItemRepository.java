package com.example.YumDash.Repository;

import com.example.YumDash.Model.Food.FoodProduct;
import com.example.YumDash.Model.Food.OrderItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {


    long countByFoodProductId(int foodProductId);


    @Modifying
    @Transactional
    @Query("UPDATE OrderItem oi SET oi.foodProduct = null WHERE oi.foodProduct.id = :productId")
    void unsetFoodProductInOrderItems(@Param("productId") int productId);


    List<OrderItem> findByFoodProduct(FoodProduct foodProduct);


}
