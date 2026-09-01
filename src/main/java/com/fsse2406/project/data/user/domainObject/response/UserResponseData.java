package com.fsse2406.project.data.user.domainObject.response;

import com.fsse2406.project.data.user.entity.UserEntity;
import lombok.Data;

@Data
public class UserResponseData {
    private String uid;
    private String firebaseUid;
    private String email;

    public UserResponseData(UserEntity userEntity) {
        this.uid = userEntity.getUid();
        this.firebaseUid = userEntity.getFirebaseUid();
        this.email = userEntity.getEmail();
    }
}
