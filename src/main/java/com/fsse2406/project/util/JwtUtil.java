package com.fsse2406.project.util;

import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class JwtUtil {
    public static FirebaseUserData getFirebaseUserData(JwtAuthenticationToken jwt) {
        return new FirebaseUserData(jwt);
    }
}
