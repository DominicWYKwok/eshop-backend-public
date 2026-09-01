package com.fsse2406.project.data.user.entity;

import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "user")
public class UserEntity {
    @Id
    private String uid;

    private String firebaseUid;

    private String email;

    public UserEntity(FirebaseUserData firebaseUserData) {
        this.firebaseUid = firebaseUserData.getFirebaseUid();
        this.email = firebaseUserData.getEmail();
    }
}
