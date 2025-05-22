package com.example.YumDash.Service;


import com.example.YumDash.Model.User.User;
import com.example.YumDash.Repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PhoneService {
    private final SmsService smsService;
    private final UserRepo userRepo;

    public void startPhoneVerification(User user, String phoneNumber) {
        String formatted = smsService.formatPhoneNumber(phoneNumber);
        smsService.startPhoneVerification(formatted);
    }


    public boolean verifyCode(User user, String phoneNumber, String code) {
        String formatted = smsService.formatPhoneNumber(phoneNumber);
        boolean verified = smsService.checkVerificationCode(formatted, code);
        if (verified) {
            user.setPhoneNumber(formatted);
            user.setPhoneVerified(true);
            userRepo.save(user);
        }
        return verified;
    }
}
