package com.fsse2406.project.service;

import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import com.fsse2406.project.data.user.domainObject.request.UserPatchRequestData;
import com.fsse2406.project.data.user.entity.UserEntity;

public interface UserService {
    UserEntity getEntityByFirebaseUserData(FirebaseUserData firebaseUserData);

    UserEntity createOrSyncEntityByFirebaseUserData(FirebaseUserData firebaseUserData);

    UserEntity getExistingEntityByFirebaseUserData(FirebaseUserData firebaseUserData);

    UserEntity updateProfile(FirebaseUserData firebaseUserData, UserPatchRequestData requestData);
}
