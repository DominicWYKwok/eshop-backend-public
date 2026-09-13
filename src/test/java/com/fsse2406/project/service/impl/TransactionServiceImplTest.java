package com.fsse2406.project.service.impl;

import com.fsse2406.project.data.cart.status.TransactionStatus;
import com.fsse2406.project.data.transaction.domainObject.TransactionResponseData;
import com.fsse2406.project.data.transaction.entity.TransactionEntity;
import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import com.fsse2406.project.data.user.entity.UserEntity;
import com.fsse2406.project.repository.TransactionRepository;
import com.fsse2406.project.service.CartItemService;
import com.fsse2406.project.service.MongoDBService;
import com.fsse2406.project.service.ProductService;
import com.fsse2406.project.service.StripeService;
import com.fsse2406.project.service.TransactionProductService;
import com.fsse2406.project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionProductService transactionProductService;
    @Mock
    private UserService userService;
    @Mock
    private CartItemService cartItemService;
    @Mock
    private ProductService productService;
    @Mock
    private StripeService stripeService;
    @Mock
    private MongoDBService mongoDBService;

    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionServiceImpl(
                transactionRepository,
                transactionProductService,
                userService,
                cartItemService,
                productService,
                stripeService,
                mongoDBService
        );
    }

    @Test
    void getAllSuccessTransactionsReturnsNewestFirstWithItems() {
        FirebaseUserData firebaseUserData = firebaseUserData();
        UserEntity user = new UserEntity();
        user.setFirebaseUid("firebase-user-1");

        TransactionEntity newest = transaction("transaction-2", user, LocalDateTime.of(2026, 9, 8, 10, 30));
        TransactionEntity oldest = transaction("transaction-1", user, LocalDateTime.of(2026, 9, 7, 9, 0));

        when(userService.getExistingEntityByFirebaseUserData(firebaseUserData)).thenReturn(user);
        when(transactionRepository.findByUserAndStatusOrderByDatetimeDesc(user, TransactionStatus.SUCCESS))
                .thenReturn(List.of(newest, oldest));
        when(transactionProductService.getAllTransactionProductsByTransation(newest)).thenReturn(List.of());
        when(transactionProductService.getAllTransactionProductsByTransation(oldest)).thenReturn(List.of());

        List<TransactionResponseData> result = transactionService.getAllSuccessTransactions(firebaseUserData);

        assertEquals(List.of("transaction-2", "transaction-1"),
                result.stream().map(TransactionResponseData::getTid).toList());
        verify(transactionRepository)
                .findByUserAndStatusOrderByDatetimeDesc(user, TransactionStatus.SUCCESS);
        verify(transactionProductService).getAllTransactionProductsByTransation(newest);
        verify(transactionProductService).getAllTransactionProductsByTransation(oldest);
    }

    private TransactionEntity transaction(String tid, UserEntity user, LocalDateTime datetime) {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setTid(tid);
        transactionEntity.setUser(user);
        transactionEntity.setDatetime(datetime);
        transactionEntity.setStatus(TransactionStatus.SUCCESS);
        return transactionEntity;
    }

    private FirebaseUserData firebaseUserData() {
        Jwt jwt = Jwt.withTokenValue("firebase-token")
                .header("alg", "RS256")
                .claim("user_id", "firebase-user-1")
                .claim("email", "user@example.com")
                .claim("email_verified", true)
                .build();
        return new FirebaseUserData(new JwtAuthenticationToken(jwt));
    }
}
