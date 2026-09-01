package com.fsse2406.project.api;

import com.fsse2406.project.config.EnvConfig;
import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@CrossOrigin({EnvConfig.DEV_BASE_URL, EnvConfig.PROD_BASE_URL})
public class UserApi {
    @GetMapping("/me/details")
    public FirebaseUserData getMyUserDetails(JwtAuthenticationToken jwtToken){
        FirebaseUserData loginUser = new FirebaseUserData(jwtToken);
        return loginUser;
    }
}
