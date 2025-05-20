package com.example.YumDash.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {


    private final JavaMailSender javaMailSender;

    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    public void sendPasswordChangedConfirmationEmail(String toEmail) {
        String subject = "Parola a fost schimbată";

        String htmlMessage = "<html>" +
                "<body style='font-family: Arial, sans-serif;'>" +
                "<h2 style='color: #2d7dd2;'>Confirmare schimbare parolă</h2>" +
                "<p>Bună,</p>" +
                "<p>Parola contului tău a fost schimbată cu succes.</p>" +
                "<p>Dacă <b>nu tu</b> ai făcut această schimbare, te rugăm să ne contactezi imediat: <br>" +
                "<strong>eternalsq1337@gmail.com</strong> sau <strong>0751777343</strong></p>" +
                "<a href='http://localhost:8080/loginForm' " +
                "style='display: inline-block; padding: 10px 20px; background-color: #2d7dd2; color: white; text-decoration: none; border-radius: 5px;'>Autentifică-te</a>" +
                "<br><br>" +
                "<img src='https://i.postimg.cc/xT6FcJG6/Yum-Dash-Logo.jpg' alt='Logo aplicație' width='150'/>" +
                "</body>" +
                "</html>";
        try {
            sendHtmlEmail(toEmail, subject, htmlMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Eroare la trimiterea emailului de confirmare parolă", e);
        }
    }

    public void sendResetPasswordEmail(String toEmail, String resetLink) {
        String subject = "Resetare parolă";

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
            sendHtmlEmail(toEmail, subject, htmlMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Eroare la trimiterea emailului de resetare parolă", e);
        }
    }


    public void sendVerificationEmail(String toEmail, String verificationCode) {
        String subject = "Codul de verificare YumDash";

        String htmlContent = "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                "<h2>Salut!</h2>" +
                "<p>Codul tău de verificare este: <strong style='font-size: 20px;'>" + verificationCode + "</strong></p>" +
                "<p>Te rugăm să îl introduci pentru a activa contul.</p>" +
                "<br><p>Mulțumim,<br>Echipa YumDash</p>" +
                "</div>";
        try {
            sendHtmlEmail(toEmail, subject, htmlContent);
        } catch (MessagingException e) {
            throw new RuntimeException("Eroare la trimiterea emailului de verificare", e);
        }
    }


}
