package com.example.YumDash.Service;

import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.OrderRepo;
import com.example.YumDash.Repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private UserRepo userRepo;


    @Test
    public void testAcceptOrderNoInput() {

        assertThrows(NoSuchElementException.class, () -> orderService.acceptOrder(-1));
    }

    @Test
    public void testAcceptOrderOneInput() {

        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("1234");
        userRepo.save(user);

        UserOrder order = new UserOrder();
        order.setUser(user);
        order.setStatus("IN_ASTEPTARE");
        orderRepo.save(order);

        orderService.acceptOrder(order.getId());

        UserOrder updatedOrder = orderRepo.findById(order.getId()).orElseThrow();
        assertEquals("CONFIRMATA", updatedOrder.getStatus());
    }

    @Test
    public void testAcceptOrderTwoInputs() {

        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("1234");
        userRepo.save(user);

        UserOrder order1 = new UserOrder();
        order1.setUser(user);
        order1.setStatus("IN_ASTEPTARE");
        orderRepo.save(order1);

        UserOrder order2 = new UserOrder();
        order2.setUser(user);
        order2.setStatus("IN_ASTEPTARE");
        orderRepo.save(order2);

        orderService.acceptOrder(order1.getId());
        orderService.acceptOrder(order2.getId());

        UserOrder updatedOrder1 = orderRepo.findById(order1.getId()).orElseThrow();
        UserOrder updatedOrder2 = orderRepo.findById(order2.getId()).orElseThrow();
        assertEquals("CONFIRMATA", updatedOrder1.getStatus());
        assertEquals("CONFIRMATA", updatedOrder2.getStatus());
    }

    @Test
    public void testAcceptOrderWrongInput() {

        assertThrows(NoSuchElementException.class, () -> orderService.acceptOrder(999999999));
    }


    @Test
    public void testCancelOrderNoInput() {

        assertThrows(NoSuchElementException.class, () -> orderService.cancelOrder(999));
    }

    @Test
    public void testCancelOrderOneInput() {

        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("1234");
        userRepo.save(user);

        UserOrder order = new UserOrder();
        order.setUser(user);
        order.setStatus("IN_ASTEPTARE");
        orderRepo.save(order);

        orderService.cancelOrder(order.getId());

        UserOrder updatedOrder = orderRepo.findById(order.getId()).orElseThrow();
        assertEquals("ANULATA", updatedOrder.getStatus());
    }

    @Test
    public void testCancelOrderTwoInputs() {

        User user = new User();
        user.setEmail("test@email.com");
        user.setPassword("1234");
        userRepo.save(user);

        UserOrder order1 = new UserOrder();
        order1.setUser(user);
        order1.setStatus("IN_ASTEPTARE");
        orderRepo.save(order1);

        UserOrder order2 = new UserOrder();
        order2.setUser(user);
        order2.setStatus("IN_ASTEPTARE");
        orderRepo.save(order2);

        orderService.cancelOrder(order1.getId());
        orderService.cancelOrder(order2.getId());

        UserOrder updatedOrder1 = orderRepo.findById(order1.getId()).orElseThrow();
        UserOrder updatedOrder2 = orderRepo.findById(order2.getId()).orElseThrow();
        assertEquals("ANULATA", updatedOrder1.getStatus());
        assertEquals("ANULATA", updatedOrder2.getStatus());
    }

    @Test
    public void testCancelOrderWrongInput() {

        assertThrows(NoSuchElementException.class, () -> orderService.cancelOrder(999));
    }


    @Test
    public void testFindOrdersByUserNoInput() {
        List<UserOrder> result = orderService.findOrdersByUser(null);
        assertTrue(result.isEmpty(), "Expected empty list when user is null");
    }

    @Test
    public void testFindOrdersByUserOneInput() {

        User user = new User();
        user.setEmail("user@email.com");
        user.setPassword("password");
        userRepo.save(user);

        List<UserOrder> orders = orderService.findOrdersByUser(user);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void testFindOrdersByUserTwoInputs() {

        User user = new User();
        user.setEmail("user@email.com");
        user.setPassword("password");
        userRepo.save(user);

        UserOrder order1 = new UserOrder();
        order1.setUser(user);
        order1.setStatus("IN_ASTEPTARE");
        orderRepo.save(order1);

        UserOrder order2 = new UserOrder();
        order2.setUser(user);
        order2.setStatus("IN_ASTEPTARE");
        orderRepo.save(order2);

        List<UserOrder> orders = orderService.findOrdersByUser(user);
        assertEquals(2, orders.size());
    }

    @Test
    public void testFindOrdersByUserWrongInput() {

        User user = new User();
        user.setEmail("nou@email.com");
        user.setPassword("123456");
        userRepo.save(user);

        List<UserOrder> orders = orderService.findOrdersByUser(user);
        assertTrue(orders.isEmpty());
    }


    @Test
    public void testCreateOrderNoInput() {
        assertThrows(NullPointerException.class, () -> orderService.createOrder(null));
    }

    @Test
    public void testCreateOrderOneInput() {

        User user = new User();
        user.setEmail("user@email.com");
        user.setPassword("password");
        userRepo.save(user);

        UserOrder order = new UserOrder();
        order.setUser(user);
        order.setStatus("IN_ASTEPTARE");

        orderService.createOrder(order);

        List<UserOrder> orders = orderService.findOrdersByUser(user);
        assertEquals(1, orders.size());
        assertEquals("IN_ASTEPTARE", orders.get(0).getStatus());
    }

    @Test
    public void testCreateOrderTwoInputs() {

        User user = new User();
        user.setEmail("user@email.com");
        user.setPassword("password");
        userRepo.save(user);

        UserOrder order1 = new UserOrder();
        order1.setUser(user);
        order1.setStatus("IN_ASTEPTARE");

        UserOrder order2 = new UserOrder();
        order2.setUser(user);
        order2.setStatus("IN_ASTEPTARE");

        orderService.createOrder(order1);
        orderService.createOrder(order2);

        List<UserOrder> orders = orderService.findOrdersByUser(user);
        assertEquals(2, orders.size());
    }

    @Test
    public void testCreateOrderWrongInput() {

        UserOrder order = new UserOrder();
        order.setUser(null);
        order.setStatus("IN_ASTEPTARE");

        assertThrows(NoSuchElementException.class, () -> orderService.createOrder(order));
    }
}
