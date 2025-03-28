package com.example.YumDash.Service;

import com.example.YumDash.Model.Order;
import com.example.YumDash.Model.User;
import com.example.YumDash.Repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    public List<Order> findOrdersByUserId(int userId) {
        return orderRepo.findByUserId(userId);
    }
}
