package com.example.YumDash.Service;



import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepo orderRepo;


    public void acceptOrder(int id) {
        UserOrder order = orderRepo.findById(id).orElseThrow();
        order.setStatus("CONFIRMATA");
        orderRepo.save(order);
    }

    public void cancelOrder(int id) {
        UserOrder order = orderRepo.findById(id).orElseThrow();
        order.setStatus("ANULATA");
        orderRepo.save(order);
    }

    public List<UserOrder> getOrdersByFilters(String email, String status, LocalDate date) {
        if (email != null && email.isBlank()) email = null;
        if (status != null && status.isBlank()) status = null;

        Timestamp startTimestamp;
        Timestamp endTimestamp;

        if (date == null) {
            startTimestamp = Timestamp.valueOf(LocalDate.of(1970, 1, 1).atStartOfDay());
            endTimestamp = Timestamp.valueOf(LocalDate.of(3000, 1, 1).atStartOfDay());
        } else {
            startTimestamp = Timestamp.valueOf(date.atStartOfDay());
            endTimestamp = Timestamp.valueOf(date.plusDays(1).atStartOfDay());
        }

        return orderRepo.findByFilters(email, status, startTimestamp, endTimestamp);
    }



    public void updateOrderStatus(int orderId, String status) {
        UserOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Comanda nu a fost găsită"));
        order.setStatus(status);
        orderRepo.save(order);
    }

    public List<UserOrder> getAllOrders() {return orderRepo.findAll();}

    public List<UserOrder> findOrdersByUser(User user) {
        return orderRepo.findByUserOrderByOrderDateAsc(user);
    }

    public void createOrder(UserOrder userOrder){orderRepo.save(userOrder);
    }

    public Optional<UserOrder> findById(int id) {
        return orderRepo.findById(id);
    }

    public void save(UserOrder order) {
        orderRepo.save(order);
    }

}
