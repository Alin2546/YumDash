package com.example.YumDash.Service;

import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;
    public List<UserOrder> findOrdersByUser(User user) {
        return orderRepo.findByUser(user);
    }

    public void createOrder(UserOrder userOrder){orderRepo.save(userOrder);
    }
}
