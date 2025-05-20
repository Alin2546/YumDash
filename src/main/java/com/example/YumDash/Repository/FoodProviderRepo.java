package com.example.YumDash.Repository;

import com.example.YumDash.Model.Food.FoodProvider;
import com.example.YumDash.Model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodProviderRepo extends JpaRepository<FoodProvider, Integer> {
    List<FoodProvider> findByNameContainingIgnoreCase(String keyword);
    Optional<FoodProvider> findByEmail(String email);
    FoodProvider findByUser(User user);

    @Query("SELECT fp FROM FoodProvider fp WHERE fp.user.isActive = :isActive")
    List<FoodProvider> findByUserIsActive(@Param("isActive") boolean isActive);



}
