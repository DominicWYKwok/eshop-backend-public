package com.fsse2406.project.data.user.domainObject.response;

import com.fsse2406.project.data.user.entity.UserEntity;
import lombok.Data;

import java.time.Instant;

@Data
public class UserResponseData {
    private String uid;
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

    public UserResponseData(UserEntity userEntity) {
        this.uid = userEntity.getUid();
        this.firebaseUid = userEntity.getFirebaseUid();
        this.email = userEntity.getEmail();
        this.emailVerified = userEntity.isEmailVerified();
        this.nickname = userEntity.getNickname();
        this.userIcon = userEntity.getUserIcon();
        this.phoneNumber = userEntity.getPhoneNumber();
        this.address = userEntity.getAddress();
        this.marketingPreference = userEntity.isMarketingPreference();
        this.createdAt = userEntity.getCreatedAt();
        this.updatedAt = userEntity.getUpdatedAt();
    }
}
