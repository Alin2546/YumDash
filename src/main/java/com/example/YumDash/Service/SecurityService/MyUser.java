package com.example.YumDash.Service.SecurityService;

import com.example.YumDash.Model.User.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.List;


@RequiredArgsConstructor
@Getter
public class MyUser implements UserDetails {

    private final User user;
    private int id;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public String getName() {
        return user.getName();
    }

    @Override
    public boolean isEnabled() { return user.isActive(); }

    public String getPhoneNumber() {
        return user.getPhoneNumber();
    }

}
