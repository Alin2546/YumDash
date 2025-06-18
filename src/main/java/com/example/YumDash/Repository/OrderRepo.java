package com.example.YumDash.Repository;

import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepo extends JpaRepository<UserOrder, Integer> {
    List<UserOrder> findByFoodProvider(FoodProvider provider);
    List<UserOrder> findByUserOrderByOrderDateAsc(User user);

    @Query(value = "SELECT uo.* FROM user_orders uo " +
            "JOIN users u ON u.id = uo.user_id " +
            "WHERE (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "AND (:status IS NULL OR uo.status = :status) " +
            "AND uo.order_date >= :startDate " +
            "AND uo.order_date < :endDate", nativeQuery = true)
    List<UserOrder> findByFilters(@Param("email") String email,
                                  @Param("status") String status,
                                  @Param("startDate") Timestamp startDate,
                                  @Param("endDate") Timestamp endDate);


    List<UserOrder> findByFoodProviderAndStatus(FoodProvider foodProvider, String livrata);
}
