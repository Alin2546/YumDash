package com.example.YumDash.Service;

import com.example.YumDash.Model.User.User;

import com.example.YumDash.Repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private FoodProviderRepo foodProviderRepo;

    @Autowired
    private FoodProductRepo foodProductRepo;

    @Test
    public void testCreateUser_NoInput() {
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(null));
    }

    @Test
    public void testCreateUser_OneValidUser() {
        User user = new User();
        user.setEmail("one@test.com");
        user.setPassword("pass");
        userService.createUser(user);

        User saved = userRepo.findByEmail("one@test.com").orElse(null);
        assertNotNull(saved);
    }

    @Test
    public void testCreateUser_TwoValidUsers() {
        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setPassword("pass1");

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setPassword("pass2");

        userService.createUser(user1);
        userService.createUser(user2);

        assertEquals(2, userRepo.findAll().size());
    }

    @Test
    public void testCreateUser_WrongInput_MissingEmail() {
        User user = new User();
        user.setPassword("pass");
        assertThrows(Exception.class, () -> userService.createUser(user));
    }

    @Test
    public void testUpdateUser_NoInput() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userService.updateUser(null));
    }

    @Test
    public void testUpdateUser_OneValid() {
        User user = new User();
        user.setEmail("valid@test.com");
        user.setPassword("123");
        userRepo.save(user);

        user.setEmail("updated@test.com");
        userService.updateUser(user);

        assertEquals("updated@test.com", userRepo.findById(user.getId()).get().getEmail());
    }

    @Test
    public void testUpdateUser_TwoUsers() {
        User u1 = new User();
        u1.setEmail("u1@test.com");
        u1.setPassword("1");
        User u2 = new User();
        u2.setEmail("u2@test.com");
        u2.setPassword("2");
        userRepo.saveAll(List.of(u1, u2));

        u1.setEmail("new1@test.com");
        u2.setEmail("new2@test.com");

        userService.updateUser(u1);
        userService.updateUser(u2);

        assertEquals("new1@test.com", userRepo.findById(u1.getId()).get().getEmail());
        assertEquals("new2@test.com", userRepo.findById(u2.getId()).get().getEmail());
    }

    @Test
    public void testUpdateUser_WrongInput_InvalidId() {
        User fake = new User();
        fake.setId(999999);
        fake.setEmail("fail@test.com");
        assertThrows(Exception.class, () -> userService.updateUser(fake));
    }

    @Test
    public void testUpdateUserStatus_NoInput() {
        assertThrows(Exception.class, () -> userService.updateUserStatus(0, true));
    }

    @Test
    public void testUpdateUserStatus_OneValidUser() {
        User u = new User();
        u.setEmail("status@test.com");
        u.setPassword("p");
        userRepo.save(u);
        userService.updateUserStatus(u.getId(), true);
        assertTrue(userRepo.findById(u.getId()).get().isActive());
    }

    @Test
    public void testUpdateUserStatus_TwoUsers() {
        User u1 = new User();
        u1.setEmail("u1@test.com");
        u1.setPassword("p1");
        User u2 = new User();
        u2.setEmail("u2@test.com");
        u2.setPassword("p2");
        userRepo.saveAll(List.of(u1, u2));

        userService.updateUserStatus(u1.getId(), true);
        userService.updateUserStatus(u2.getId(), false);

        assertTrue(userRepo.findById(u1.getId()).get().isActive());
        assertFalse(userRepo.findById(u2.getId()).get().isActive());
    }

    @Test
    public void testUpdateUserStatus_WrongInput() {
        assertThrows(RuntimeException.class, () -> userService.updateUserStatus(-1, true));
    }

    @Test
    public void testDeleteUserById_NoInput() {
        assertThrows(Exception.class, () -> userService.deleteUserById(0));
    }

    @Test
    public void testDeleteUserById_OneValid() {
        User u = new User();
        u.setEmail("del@test.com");
        u.setPassword("p");
        userRepo.save(u);
        userService.deleteUserById(u.getId());
        assertFalse(userRepo.findById(u.getId()).isPresent());
    }

    @Test
    public void testDeleteUserById_Many() {
        User u1 = new User();
        u1.setEmail("u1@t.com");
        u1.setPassword("p1");
        User u2 = new User();
        u2.setEmail("u2@t.com");
        u2.setPassword("p2");
        userRepo.saveAll(List.of(u1, u2));

        userService.deleteUserById(u1.getId());
        userService.deleteUserById(u2.getId());

        assertTrue(userRepo.findAll().isEmpty());
    }

    @Test
    public void testDeleteUserById_WrongInput_InvalidId() {
        assertThrows(RuntimeException.class, () -> userService.deleteUserById(999999));
    }

    @Test
    public void testGenerateVerificationCode_NoInput() {
        String code = userService.generateVerificationCode();
        assertNotNull(code);
        assertEquals(6, code.length());
    }

    @Test
    public void testGenerateVerificationCode_OneCall() {
        String code = userService.generateVerificationCode();
        assertTrue(code.matches("\\d{6}"));
    }

    @Test
    public void testGenerateVerificationCode_ManyCalls() {
        String code1 = userService.generateVerificationCode();
        String code2 = userService.generateVerificationCode();
        assertNotEquals(code1, code2);
    }

    @Test
    public void testGenerateVerificationCode_WrongInput_NotApplicable() {

        String code = userService.generateVerificationCode();
        assertDoesNotThrow(() -> Integer.parseInt(code));
    }

}
