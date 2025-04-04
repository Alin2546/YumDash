package com.example.YumDash.Service.SecurityService;


import com.example.YumDash.Model.User.User;
import com.example.YumDash.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(username).orElseThrow(()-> new RuntimeException("User not found" + username));
        if (user == null) {
            throw new UsernameNotFoundException("Invalid email or password");
        }
        return new MyUser(user);
    }
}
