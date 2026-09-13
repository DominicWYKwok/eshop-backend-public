package com.fsse2406.project.util;

import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

public class JwtUtil {
    private JwtUtil() {
    }

    public static FirebaseUserData getFirebaseUserData(JwtAuthenticationToken jwt) {
        return new FirebaseUserData(jwt);
    }

    public static FirebaseUserData getVerifiedFirebaseUserData(JwtAuthenticationToken jwt) {
        FirebaseUserData firebaseUserData = getFirebaseUserData(jwt);
        if (!firebaseUserData.isEmailVerified()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Email address is not verified"
            );
        }
        return firebaseUserData;
    }
}
