package com.example.YumDash.Service;

import com.example.YumDash.Model.User.User;
import com.example.YumDash.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo usersRepo;
    private final PasswordEncoder passwordEncoder;

    public void createUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        usersRepo.save(user);
    }

    public User findByEmail(String email) {return usersRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("Emailul nu a fost gasit:" + email));}




}
