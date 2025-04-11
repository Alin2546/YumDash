package com.example.YumDash.Service;

import com.example.YumDash.Model.User.User;
import com.example.YumDash.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


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

    public Optional<User> findByEmail(String email) {
        return usersRepo.findByEmail(email);
    }

    public void resetPassword(String email, String newPassword) {

        User user = usersRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilizatorul cu acest email nu a fost găsit"));

        user.setPassword(passwordEncoder.encode(newPassword));

        usersRepo.save(user);
    }


}
