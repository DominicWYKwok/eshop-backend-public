package com.fsse2406.project.service.impl;

import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import com.fsse2406.project.data.user.domainObject.request.UserPatchRequestData;
import com.fsse2406.project.data.user.entity.UserEntity;
import com.fsse2406.project.repository.UserRepository;
import com.fsse2406.project.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity getEntityByFirebaseUserData(FirebaseUserData firebaseUserData) {
        return userRepository.findByFirebaseUid(firebaseUserData.getFirebaseUid())
                .orElseGet(() -> userRepository.save(new UserEntity(firebaseUserData)));
    }

    @Override
    public UserEntity createOrSyncEntityByFirebaseUserData(FirebaseUserData firebaseUserData) {
        Optional<UserEntity> optionalUserEntity = userRepository.findByFirebaseUid(firebaseUserData.getFirebaseUid());
        if (optionalUserEntity.isEmpty()) {
            UserEntity userEntity = new UserEntity(firebaseUserData);
            return userRepository.save(userEntity);
        }

        UserEntity userEntity = optionalUserEntity.get();
        boolean firebaseDataChanged = !Objects.equals(userEntity.getEmail(), firebaseUserData.getEmail())
                || userEntity.isEmailVerified() != firebaseUserData.isEmailVerified();

        if (!firebaseDataChanged) {
            return userEntity;
        }

        userEntity.setEmail(firebaseUserData.getEmail());
        userEntity.setEmailVerified(firebaseUserData.isEmailVerified());
        userEntity.setUpdatedAt(Instant.now());
        return userRepository.save(userEntity);
    }

    @Override
    public UserEntity getExistingEntityByFirebaseUserData(FirebaseUserData firebaseUserData) {
        return userRepository.findByFirebaseUid(firebaseUserData.getFirebaseUid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));
    }

    @Override
    public UserEntity updateProfile(FirebaseUserData firebaseUserData, UserPatchRequestData requestData) {
        UserEntity userEntity = getExistingEntityByFirebaseUserData(firebaseUserData);

        if (requestData.getNickname() != null) {
            userEntity.setNickname(requestData.getNickname().trim());
        }
        if (requestData.getUserIcon() != null) {
            userEntity.setUserIcon(requestData.getUserIcon().trim());
        }
        if (requestData.getPhoneNumber() != null) {
            userEntity.setPhoneNumber(requestData.getPhoneNumber().trim());
        }
        if (requestData.getAddress() != null) {
            userEntity.setAddress(requestData.getAddress().trim());
        }
        if (requestData.getMarketingPreference() != null) {
            userEntity.setMarketingPreference(requestData.getMarketingPreference());
        }

        userEntity.setUpdatedAt(Instant.now());
        return userRepository.save(userEntity);
    }

}
