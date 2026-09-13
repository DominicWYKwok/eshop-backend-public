package com.fsse2406.project.data.user.entity;

import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@Document(collection = "user")
public class UserEntity {
    @Id
    private String uid;

    @Indexed(unique = true)
    private String firebaseUid;

    private String email;

    private boolean emailVerified;

    private String nickname;

    private String userIcon;

    private String phoneNumber;

    private String address;

    private boolean marketingPreference;

    private Instant createdAt;

    private Instant updatedAt;

    public UserEntity(FirebaseUserData firebaseUserData) {
        this.firebaseUid = firebaseUserData.getFirebaseUid();
        this.email = firebaseUserData.getEmail();
        this.emailVerified = firebaseUserData.isEmailVerified();
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}
