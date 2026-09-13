package com.fsse2406.project.service.impl;

import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import com.fsse2406.project.data.user.domainObject.request.UserPatchRequestData;
import com.fsse2406.project.data.user.entity.UserEntity;
import com.fsse2406.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createOrSyncCreatesUserWithFirebaseSnapshotAndCreationTime() {
        FirebaseUserData firebaseUserData = firebaseUserData("firebase-user-1", "user@example.com", false);
        when(userRepository.findByFirebaseUid("firebase-user-1")).thenReturn(Optional.empty());
        when(userRepository.save(org.mockito.ArgumentMatchers.any(UserEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserEntity result = userService.createOrSyncEntityByFirebaseUserData(firebaseUserData);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity savedUser = captor.getValue();
        assertSame(savedUser, result);
        assertEquals("firebase-user-1", savedUser.getFirebaseUid());
        assertEquals("user@example.com", savedUser.getEmail());
        assertFalse(savedUser.isEmailVerified());
        assertNotNull(savedUser.getCreatedAt());
        assertNotNull(savedUser.getUpdatedAt());
        assertEquals(savedUser.getCreatedAt(), savedUser.getUpdatedAt());
    }

    @Test
    void createOrSyncUpdatesFirebaseSnapshotWithoutOverwritingProfileOrCreationTime() {
        FirebaseUserData firebaseUserData = firebaseUserData("firebase-user-1", "new@example.com", true);
        Instant createdAt = Instant.parse("2026-09-01T00:00:00Z");
        Instant previousUpdatedAt = Instant.parse("2026-09-02T00:00:00Z");
        UserEntity existingUser = new UserEntity();
        existingUser.setFirebaseUid("firebase-user-1");
        existingUser.setEmail("old@example.com");
        existingUser.setEmailVerified(false);
        existingUser.setNickname("Dominic");
        existingUser.setUserIcon("https://example.com/user.png");
        existingUser.setCreatedAt(createdAt);
        existingUser.setUpdatedAt(previousUpdatedAt);

        when(userRepository.findByFirebaseUid("firebase-user-1")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserEntity result = userService.createOrSyncEntityByFirebaseUserData(firebaseUserData);

        assertSame(existingUser, result);
        assertEquals("new@example.com", result.getEmail());
        assertTrue(result.isEmailVerified());
        assertEquals("Dominic", result.getNickname());
        assertEquals("https://example.com/user.png", result.getUserIcon());
        assertEquals(createdAt, result.getCreatedAt());
        assertTrue(result.getUpdatedAt().isAfter(previousUpdatedAt));
        verify(userRepository).save(existingUser);
    }

    @Test
    void createOrSyncDoesNotWriteWhenFirebaseSnapshotIsUnchanged() {
        FirebaseUserData firebaseUserData = firebaseUserData("firebase-user-1", "user@example.com", true);
        UserEntity existingUser = new UserEntity();
        existingUser.setFirebaseUid("firebase-user-1");
        existingUser.setEmail("user@example.com");
        existingUser.setEmailVerified(true);

        when(userRepository.findByFirebaseUid("firebase-user-1")).thenReturn(Optional.of(existingUser));

        UserEntity result = userService.createOrSyncEntityByFirebaseUserData(firebaseUserData);

        assertSame(existingUser, result);
        verify(userRepository, never()).save(existingUser);
    }

    @Test
    void getEntityDoesNotOverwriteExistingSnapshotFromAnOlderToken() {
        FirebaseUserData staleFirebaseUserData = firebaseUserData("firebase-user-1", "user@example.com", false);
        UserEntity existingUser = new UserEntity();
        existingUser.setFirebaseUid("firebase-user-1");
        existingUser.setEmail("user@example.com");
        existingUser.setEmailVerified(true);

        when(userRepository.findByFirebaseUid("firebase-user-1")).thenReturn(Optional.of(existingUser));

        UserEntity result = userService.getEntityByFirebaseUserData(staleFirebaseUserData);

        assertSame(existingUser, result);
        assertTrue(result.isEmailVerified());
        verify(userRepository, never()).save(existingUser);
    }

    @Test
    void getExistingEntityReturnsProfileWithoutCreatingIt() {
        FirebaseUserData firebaseUserData = firebaseUserData("firebase-user-1", "user@example.com", false);
        UserEntity existingUser = new UserEntity();
        existingUser.setFirebaseUid("firebase-user-1");
        when(userRepository.findByFirebaseUid("firebase-user-1")).thenReturn(Optional.of(existingUser));

        UserEntity result = userService.getExistingEntityByFirebaseUserData(firebaseUserData);

        assertSame(existingUser, result);
        verify(userRepository, never()).save(existingUser);
    }

    @Test
    void getExistingEntityThrowsNotFoundWhenProfileDoesNotExist() {
        FirebaseUserData firebaseUserData = firebaseUserData("firebase-user-1", "user@example.com", false);
        when(userRepository.findByFirebaseUid("firebase-user-1")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.getExistingEntityByFirebaseUserData(firebaseUserData)
        );

        assertEquals(404, exception.getStatusCode().value());
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(UserEntity.class));
    }

    @Test
    void updateProfileOnlyChangesProvidedFields() {
        FirebaseUserData firebaseUserData = firebaseUserData("firebase-user-1", "user@example.com", false);
        Instant previousUpdatedAt = Instant.parse("2026-09-02T00:00:00Z");
        UserEntity existingUser = new UserEntity();
        existingUser.setFirebaseUid("firebase-user-1");
        existingUser.setNickname("Dominic");
        existingUser.setPhoneNumber("+852 9123 4567");
        existingUser.setAddress("Hong Kong");
        existingUser.setMarketingPreference(true);
        existingUser.setUpdatedAt(previousUpdatedAt);

        UserPatchRequestData requestData = new UserPatchRequestData();
        requestData.setNickname("  Dom  ");
        requestData.setAddress("  Kowloon, Hong Kong  ");
        requestData.setMarketingPreference(false);

        when(userRepository.findByFirebaseUid("firebase-user-1")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        UserEntity result = userService.updateProfile(firebaseUserData, requestData);

        assertSame(existingUser, result);
        assertEquals("Dom", result.getNickname());
        assertEquals("+852 9123 4567", result.getPhoneNumber());
        assertEquals("Kowloon, Hong Kong", result.getAddress());
        assertFalse(result.isMarketingPreference());
        assertTrue(result.getUpdatedAt().isAfter(previousUpdatedAt));
        verify(userRepository).save(existingUser);
    }

    private FirebaseUserData firebaseUserData(String firebaseUid, String email, boolean emailVerified) {
        Jwt jwt = Jwt.withTokenValue("firebase-token")
                .header("alg", "RS256")
                .claim("user_id", firebaseUid)
                .claim("email", email)
                .claim("email_verified", emailVerified)
                .build();
        return new FirebaseUserData(new JwtAuthenticationToken(jwt));
    }
}
