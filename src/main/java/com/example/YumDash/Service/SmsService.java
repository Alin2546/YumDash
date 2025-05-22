package com.example.YumDash.Service;

import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.verify.service.sid}")
    private String verifyServiceSid;


    private void initTwilio() {
        Twilio.init(accountSid, authToken);
    }

    public void startPhoneVerification(String phoneNumber) {
        initTwilio();

        String formattedNumber = formatPhoneNumber(phoneNumber);

        Verification verification = Verification.creator(
                verifyServiceSid,
                formattedNumber,
                "sms"
        ).create();

        System.out.println("Verification started with SID: " + verification.getSid());
    }

    public boolean checkVerificationCode(String phoneNumber, String code) {
        initTwilio();
        String formattedNumber = formatPhoneNumber(phoneNumber);

        VerificationCheck verificationCheck = VerificationCheck.creator(verifyServiceSid)
                .setTo(formattedNumber)
                .setCode(code)
                .create();

        return "approved".equalsIgnoreCase(verificationCheck.getStatus());
    }

    public String formatPhoneNumber(String phone) {
        if (phone == null) {
            throw new IllegalArgumentException("Număr de telefon lipsă");
        }

        phone = phone.replaceAll("\\s+", "").trim();
        if (phone.matches("^0\\d{9}$")) {
            return "+40" + phone.substring(1);
        }

        if (phone.matches("^\\d{9}$")) {
            return "+40" + phone;
        }

        if (phone.matches("^\\+40\\d{9}$")) {
            return phone;
        }

        if (phone.matches("^40\\d{9}$")) {
            return "+" + phone;
        }

        throw new IllegalArgumentException("Număr de telefon invalid: " + phone);
    }

}