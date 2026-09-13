package com.fsse2406.project.api;

import com.fsse2406.project.service.CartItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CartItemApiTest {

    @Mock
    private CartItemService cartItemService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new CartItemApi(cartItemService)).build();
    }

    @Test
    void putCartItemAllowsAuthenticatedUserWithUnverifiedEmail() throws Exception {
        Jwt jwt = Jwt.withTokenValue("firebase-token")
                .header("alg", "RS256")
                .claim("user_id", "firebase-user-1")
                .claim("email", "user@example.com")
                .claim("email_verified", false)
                .build();

        mockMvc.perform(put("/cart/product-1/2")
                        .principal(new JwtAuthenticationToken(jwt)))
                .andExpect(status().isOk());

        verify(cartItemService).putCartItem(
                eq("product-1"),
                eq(2),
                argThat(firebaseUserData ->
                        "firebase-user-1".equals(firebaseUserData.getFirebaseUid())
                                && !firebaseUserData.isEmailVerified()
                )
        );
    }
}
