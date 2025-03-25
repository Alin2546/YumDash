package com.example.YumDash.Service;

import com.example.YumDash.Model.Dto.UserLoginDto;
import com.example.YumDash.Model.Order;
import com.example.YumDash.Model.User;
import com.example.YumDash.Repository.OrderRepo;
import com.example.YumDash.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo usersRepo;
    private final OrderRepo orderRepo;

    public void createUser(User user) {
        usersRepo.save(user);
    }

    public void createOrder(Order order) {
        orderRepo.save(order);
    }

    public boolean authenticateUser(UserLoginDto userLoginDto) {
        User user = usersRepo.findByEmail(userLoginDto.getEmail());
        return user != null && user.getPassword().equals(userLoginDto.getPassword());
    }

    public User findByEmail(String email) {
        return usersRepo.findByEmail(email);
    }

    public List<Order> getOrders() {
        return orderRepo.findAll((Sort.by(Sort.Direction.ASC, "orderDate")));
    }



}
