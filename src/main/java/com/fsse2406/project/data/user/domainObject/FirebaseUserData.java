package com.fsse2406.project.data.user.domainObject;

import lombok.Data;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Data
public class FirebaseUserData {
    private String firebaseUid;
    private String email;
    private boolean emailVerified;

    public FirebaseUserData(JwtAuthenticationToken jwt) {
        this.firebaseUid = (String) jwt.getTokenAttributes().get("user_id");
        this.email = (String) jwt.getTokenAttributes().get("email");
        this.emailVerified = Boolean.TRUE.equals(
                jwt.getTokenAttributes().get("email_verified")
        );
    }
}
