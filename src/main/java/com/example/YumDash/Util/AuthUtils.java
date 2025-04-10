package com.example.YumDash.Util;

import com.example.YumDash.Service.SecurityService.MyUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class AuthUtils {

    public static String extractUsername(Object principal) {
        if (principal instanceof MyUser myUser) {
            return myUser.getUsername();
        } else if (principal instanceof OidcUser oidcUser) {
            return oidcUser.getEmail();
        } else if (principal instanceof OAuth2User oauth2User) {
            return oauth2User.getAttributes().get("name").toString();
        }
        return null;
    }
}

