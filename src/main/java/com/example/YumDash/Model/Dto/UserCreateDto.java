package com.example.YumDash.Model.Dto;

import com.example.YumDash.Model.User;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Getter
@Setter
public class UserCreateDto {
    @NotNull
    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    private String name;
    private String email;
    private String password;
    private String verifypassword;


    public User mapToCustomer() {
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        return user;
    }
}
