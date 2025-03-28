package com.example.YumDash.Repository;

import com.example.YumDash.Model.Order;
import com.example.YumDash.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(int userId);
}
