package com.example.YumDash.Controller;

import com.example.YumDash.Model.Dto.ResetPasswordDto;
import com.example.YumDash.Model.Dto.UserCreateDto;
import com.example.YumDash.Model.User.User;
import com.example.YumDash.Model.User.UserOrder;
import com.example.YumDash.Repository.UserRepo;
import com.example.YumDash.Service.EmailService;
import com.example.YumDash.Service.SecurityService.MyUser;
import com.example.YumDash.Service.OrderService;
import com.example.YumDash.Service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final OrderService orderService;
    private final EmailService emailService;



    @GetMapping("/register/form")
    public String displayUserForm(Model model) {
        model.addAttribute("userCreateDto", new UserCreateDto());
        return "createUser";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Valid UserCreateDto userCreateDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userCreateDto", userCreateDto);
            return "createUser";
        } else if (userService.findByEmail(userCreateDto.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.email", "Emailul tau a fost deja inregistrat!");
            return "createUser";
        } else if (!(userCreateDto.getPassword().equals(userCreateDto.getVerifypassword()))) {
            bindingResult.rejectValue("verifypassword", "error.verifypassword", "Parolele nu se potrivesc!");
            return "createUser";
        }
        userService.createUser(userCreateDto.mapToUser());
        return "redirect:/loginForm";
    }

    @GetMapping("/loginForm")
    public String showLoginPage() {
        return "loginForm";
    }

    @GetMapping("/profile")
    public String getUserProfile(Model model, Authentication authentication) {
        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof OidcUser oidcUser) {
                String fullName = oidcUser.getFullName();
                String email = oidcUser.getEmail();

                model.addAttribute("authProvider", "Google");
                model.addAttribute("userName", fullName);
                model.addAttribute("userEmail", email);
            } else if (principal instanceof OAuth2User oauth2User) {
                String githubLogin = oauth2User.getAttribute("login");
                String email = oauth2User.getAttribute("email");

                model.addAttribute("authProvider", "Facebook");
                model.addAttribute("userName", githubLogin);
                model.addAttribute("userEmail", email);
            } else if (principal instanceof MyUser myUser) {
                model.addAttribute("authProvider", "Local");
                model.addAttribute("userName", myUser.getName());
                model.addAttribute("userEmail", myUser.getUsername());
            }
        }
        return "userProfile";
    }


    @GetMapping("/orders")
    public String getUserOrders(Model model, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String email = null;

        if (principal instanceof OAuth2User) {
            email = (String) ((OAuth2User) principal).getAttributes().get("email");
        } else {
            email = authentication.getName();
        }
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        List<UserOrder> userOrders = orderService.findOrdersByUser(user);
        model.addAttribute("userOrders", userOrders);
        return "userOrders";
    }

    @GetMapping("/resetPasswordForm")
    public String resetPasswordForm(@RequestParam("email") String email, Model model) {
        model.addAttribute("resetPasswordDto", new ResetPasswordDto());
        model.addAttribute("email", email);
        return "resetPassword";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@Valid @ModelAttribute ResetPasswordDto resetPasswordDto, BindingResult result, @RequestParam("email") String email,Model model) {

        if (result.hasErrors()) {
            model.addAttribute("resetPasswordDto", resetPasswordDto);
            return "resetPassword";
        }

        if (!resetPasswordDto.getNewPassword().equals(resetPasswordDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.resetPasswordDto", "Parola și confirmarea parolei nu se potrivesc.");
            return "resetPassword";
        }

        try {
            userService.resetPassword(email, resetPasswordDto.getNewPassword());
            String subject = "Parola a fost schimbată";
            String htmlMessage = "<html>" +
                    "<body style='font-family: Arial, sans-serif;'>" +
                    "<h2 style='color: #2d7dd2;'>Confirmare schimbare parolă</h2>" +
                    "<p>Bună,</p>" +
                    "<p>Parola contului tău a fost schimbată cu succes.</p>" +
                    "<p>Dacă <b>nu tu</b> ai făcut această schimbare, te rugăm să ne contactezi imediat. eternalsq1337@gmail.com sau 0751777343</p>" +
                    "<a href='http://localhost:8080/loginForm' " +
                    "style='display: inline-block; padding: 10px 20px; background-color: #2d7dd2; color: white; text-decoration: none; border-radius: 5px;'>Autentifică-te</a>" +
                    "<br><br>" +
                    "<img src='https://i.postimg.cc/xT6FcJG6/Yum-Dash-Logo.jpg' alt='Logo aplicație' width='150'/>" +
                    "</body>" +
                    "</html>";

            emailService.sendHtmlEmail(email, subject, htmlMessage);
        } catch (RuntimeException e) {
            result.rejectValue("email", "error.resetPasswordDto", e.getMessage());
            return "resetPassword";
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return "redirect:/passwordChangedSuccess";
    }


    @GetMapping("/forgotPassword")
    public String showForgotPasswordPage() {
        return "forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            model.addAttribute("error", "Utilizatorul cu acest email nu a fost găsit.");
            return "forgotPassword";
        }
        String subject = "Resetare parolă";
        String resetLink = "http://localhost:8080/resetPasswordForm?email=" + email;


        String htmlMessage = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h2 style='color: #2d7dd2;'>Resetare parolă</h2>" +
                "<p>Bună,</p>" +
                "<p>Ai solicitat o resetare a parolei. Apasă pe butonul de mai jos pentru a continua:</p>" +
                "<a href='" + resetLink + "' " +
                "style='display: inline-block; padding: 10px 20px; background-color: #2d7dd2; color: white; text-decoration: none; border-radius: 5px;'>Resetează parola</a>" +
                "<p style='margin-top:20px;'>Dacă nu ai solicitat această acțiune, poți ignora acest mesaj.</p>" +
                "<br><img src='https://i.postimg.cc/xT6FcJG6/Yum-Dash-Logo.jpg' alt='Logo aplicație' width='150'/>" +
                "</body>" +
                "</html>";

        try {

            emailService.sendHtmlEmail(email, subject, htmlMessage);
            model.addAttribute("email", email);
            return "emailSentView";
        } catch (Exception e) {
            model.addAttribute("error", "A apărut o eroare la trimiterea emailului.");
            return "forgotPassword";
        }
    }

    @GetMapping("/passwordChangedSuccess")
    public String successPassword() {
        return "resetPasswordSuccess";
    }
}
