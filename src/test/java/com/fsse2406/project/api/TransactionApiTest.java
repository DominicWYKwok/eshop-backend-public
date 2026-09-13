package com.fsse2406.project.api;

import com.fsse2406.project.data.cart.status.TransactionStatus;
import com.fsse2406.project.data.transaction.domainObject.TransactionResponseData;
import com.fsse2406.project.data.transaction.entity.TransactionEntity;
import com.fsse2406.project.data.user.entity.UserEntity;
import com.fsse2406.project.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class TransactionApiTest {

    @Mock
    private TransactionService transactionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new TransactionApi(transactionService)).build();
    }

    @Test
    void getAllSuccessTransactionsReturnsCurrentVerifiedUsersOrders() throws Exception {
        UserEntity user = new UserEntity();
        user.setUid("mongo-user-1");
        user.setFirebaseUid("firebase-user-1");
        user.setEmail("user@example.com");

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTid("transaction-2");
        transaction.setUser(user);
        transaction.setDatetime(LocalDateTime.of(2026, 9, 8, 10, 30));
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setTotal(new BigDecimal("128.50"));

        when(transactionService.getAllSuccessTransactions(argThat(firebaseUserData ->
                "firebase-user-1".equals(firebaseUserData.getFirebaseUid())
                        && firebaseUserData.isEmailVerified()
        ))).thenReturn(List.of(new TransactionResponseData(transaction, List.of())));

        mockMvc.perform(get("/transaction").principal(authentication(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tid").value("transaction-2"))
                .andExpect(jsonPath("$[0].buyer_uid").value("mongo-user-1"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"))
                .andExpect(jsonPath("$[0].total").value(128.50))
                .andExpect(jsonPath("$[0].items").isArray());

        verify(transactionService).getAllSuccessTransactions(argThat(firebaseUserData ->
                "firebase-user-1".equals(firebaseUserData.getFirebaseUid())
                        && firebaseUserData.isEmailVerified()
        ));
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
}
