package com.fsse2406.project.api;

import com.fsse2406.project.data.user.entity.UserEntity;
import com.fsse2406.project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class UserApiTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new UserApi(userService)).build();
    }

    @Test
    void putMeCreatesOrReturnsUserFromFirebaseToken() throws Exception {
        Jwt jwt = Jwt.withTokenValue("firebase-token")
                .header("alg", "RS256")
                .claim("user_id", "firebase-user-1")
                .claim("email", "user@example.com")
                .claim("email_verified", true)
                .build();
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt);

        UserEntity user = new UserEntity();
        user.setUid("mongo-user-1");
        user.setFirebaseUid("firebase-user-1");
        user.setEmail("user@example.com");
        user.setEmailVerified(true);
        user.setNickname("Dominic");
        user.setUserIcon("https://example.com/user.png");

        when(userService.createOrSyncEntityByFirebaseUserData(argThat(firebaseUserData ->
                "firebase-user-1".equals(firebaseUserData.getFirebaseUid())
                        && "user@example.com".equals(firebaseUserData.getEmail())
                        && firebaseUserData.isEmailVerified()
        ))).thenReturn(user);

        mockMvc.perform(put("/user/me").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid").value("mongo-user-1"))
                .andExpect(jsonPath("$.firebaseUid").value("firebase-user-1"))
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.emailVerified").value(true))
                .andExpect(jsonPath("$.nickname").value("Dominic"))
                .andExpect(jsonPath("$.userIcon").value("https://example.com/user.png"));

        verify(userService).createOrSyncEntityByFirebaseUserData(argThat(firebaseUserData ->
                "firebase-user-1".equals(firebaseUserData.getFirebaseUid())
                        && "user@example.com".equals(firebaseUserData.getEmail())
                        && firebaseUserData.isEmailVerified()
        ));
    }

    @Test
    void putMeCreatesOrSyncsUserWhenEmailIsUnverified() throws Exception {
        Jwt jwt = Jwt.withTokenValue("firebase-token")
                .header("alg", "RS256")
                .claim("user_id", "firebase-user-1")
                .claim("email", "user@example.com")
                .claim("email_verified", false)
                .build();

        UserEntity user = new UserEntity();
        user.setUid("mongo-user-1");
        user.setFirebaseUid("firebase-user-1");
        user.setEmail("user@example.com");
        user.setEmailVerified(false);

        when(userService.createOrSyncEntityByFirebaseUserData(argThat(firebaseUserData ->
                "firebase-user-1".equals(firebaseUserData.getFirebaseUid())
                        && !firebaseUserData.isEmailVerified()
        ))).thenReturn(user);

        mockMvc.perform(put("/user/me")
                        .principal(new JwtAuthenticationToken(jwt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailVerified").value(false));

        verify(userService).createOrSyncEntityByFirebaseUserData(argThat(firebaseUserData ->
                "firebase-user-1".equals(firebaseUserData.getFirebaseUid())
                        && !firebaseUserData.isEmailVerified()
        ));
    }

    @Test
    void getMeReturnsExistingProfile() throws Exception {
        JwtAuthenticationToken authentication = authentication(true);
        UserEntity user = profileUser();

        when(userService.getExistingEntityByFirebaseUserData(argThat(firebaseUserData ->
                "firebase-user-1".equals(firebaseUserData.getFirebaseUid())
        ))).thenReturn(user);

        mockMvc.perform(get("/user/me").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("Dominic"))
                .andExpect(jsonPath("$.phoneNumber").value("+852 9123 4567"))
                .andExpect(jsonPath("$.address").value("Kowloon, Hong Kong"))
                .andExpect(jsonPath("$.marketingPreference").value(true));
    }

    @Test
    void patchMeUpdatesProvidedProfileFields() throws Exception {
        JwtAuthenticationToken authentication = authentication(false);
        UserEntity user = profileUser();
        user.setNickname("Dom");
        user.setMarketingPreference(false);

        when(userService.updateProfile(
                argThat(firebaseUserData -> "firebase-user-1".equals(firebaseUserData.getFirebaseUid())),
                argThat(requestData -> "Dom".equals(requestData.getNickname())
                        && Boolean.FALSE.equals(requestData.getMarketingPreference()))
        )).thenReturn(user);

        mockMvc.perform(patch("/user/me")
                        .principal(authentication)
                        .contentType("application/json")
                        .content("""
                                {
                                  "nickname": "Dom",
                                  "marketingPreference": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("Dom"))
                .andExpect(jsonPath("$.marketingPreference").value(false));
    }

    @Test
    void patchMeRejectsInvalidProfileFields() throws Exception {
        mockMvc.perform(patch("/user/me")
                        .principal(authentication(true))
                        .contentType("application/json")
                        .content("""
                                {
                                  "nickname": "   ",
                                  "phoneNumber": "   "
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userService);
    }

    private JwtAuthenticationToken authentication(boolean emailVerified) {
        Jwt jwt = Jwt.withTokenValue("firebase-token")
                .header("alg", "RS256")
                .claim("user_id", "firebase-user-1")
                .claim("email", "user@example.com")
                .claim("email_verified", emailVerified)
                .build();
        return new JwtAuthenticationToken(jwt);
    }

    private UserEntity profileUser() {
        UserEntity user = new UserEntity();
        user.setUid("mongo-user-1");
        user.setFirebaseUid("firebase-user-1");
        user.setEmail("user@example.com");
        user.setEmailVerified(true);
        user.setNickname("Dominic");
        user.setUserIcon("https://example.com/user.png");
        user.setPhoneNumber("+852 9123 4567");
        user.setAddress("Kowloon, Hong Kong");
        user.setMarketingPreference(true);
        return user;
    }
}
