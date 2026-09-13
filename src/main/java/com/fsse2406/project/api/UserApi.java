package com.fsse2406.project.api;

import com.fsse2406.project.config.EnvConfig;
import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import com.fsse2406.project.data.user.domainObject.request.UserPatchRequestData;
import com.fsse2406.project.data.user.domainObject.response.UserResponseData;
import com.fsse2406.project.data.user.entity.UserEntity;
import com.fsse2406.project.service.UserService;
import com.fsse2406.project.util.JwtUtil;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@CrossOrigin({EnvConfig.DEV_BASE_URL, EnvConfig.PROD_BASE_URL})
public class UserApi {
    private final UserService userService;

    public UserApi(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/me")
    public UserResponseData putFirebaseUser(JwtAuthenticationToken jwtToken) {
        FirebaseUserData firebaseUserData = JwtUtil.getFirebaseUserData(jwtToken);
        UserEntity userEntity = userService.createOrSyncEntityByFirebaseUserData(firebaseUserData);
        return new UserResponseData(userEntity);
    }

    @GetMapping("/me")
    public UserResponseData getMyProfile(JwtAuthenticationToken jwtToken) {
        FirebaseUserData firebaseUserData = JwtUtil.getFirebaseUserData(jwtToken);
        UserEntity userEntity = userService.getExistingEntityByFirebaseUserData(firebaseUserData);
        return new UserResponseData(userEntity);
    }

    @PatchMapping("/me")
    public UserResponseData patchMyProfile(JwtAuthenticationToken jwtToken,
                                           @Valid @RequestBody UserPatchRequestData requestData) {
        FirebaseUserData firebaseUserData = JwtUtil.getFirebaseUserData(jwtToken);
        UserEntity userEntity = userService.updateProfile(firebaseUserData, requestData);
        return new UserResponseData(userEntity);
    }

    @GetMapping("/me/details")
    public FirebaseUserData getMyUserDetails(JwtAuthenticationToken jwtToken) {
        return new FirebaseUserData(jwtToken);
    }
}
