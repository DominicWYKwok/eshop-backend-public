package com.fsse2406.project.service.impl;

import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fsse2406.project.service.*;

import com.fsse2406.project.data.cart.entity.CartItemEntity;
import com.fsse2406.project.data.cart.status.TransactionStatus;
import com.fsse2406.project.data.transaction.domainObject.TransactionResponseData;
import com.fsse2406.project.data.transaction.entity.TransactionEntity;
import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;
import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import com.fsse2406.project.data.user.entity.UserEntity;

import com.fsse2406.project.exception.transaction.TransactionNotFoundException;
import com.fsse2406.project.exception.transaction.TransactionPreparationException;
import com.fsse2406.project.exception.transaction.TransactionStatusException;

import com.fsse2406.project.repository.TransactionRepository;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final TransactionRepository transactionRepository;
    private final TransactionProductService transactionProductService;
    private final UserService userService;
    private final CartItemService cartItemService;
    private final ProductService productService;
    private final StripeService stripeService;
    private final  MongoDBService mongoDBService;

    public TransactionServiceImpl(TransactionRepository transactionRepository, TransactionProductService transactionProductService, UserService userService, CartItemService cartItemService, ProductService productService, StripeService stripeService, MongoDBService mongoDBService) {
        this.transactionRepository = transactionRepository;
        this.transactionProductService = transactionProductService;
        this.userService = userService;
        this.cartItemService = cartItemService;
        this.productService = productService;
        this.stripeService = stripeService;
        this.mongoDBService = mongoDBService;
    }

    @Override
    public TransactionResponseData prepareTransaction(FirebaseUserData firebaseUserData) {
        try {
            UserEntity userEntity = userService.getEntityByFirebaseUserData(firebaseUserData);
            List<CartItemEntity> cartItemList = cartItemService.getCartItemListByUser(userEntity);
            if (cartItemList.isEmpty()) {
                throw new TransactionPreparationException("Transaction cannot be prepared because no cart items exist");
            }

            TransactionEntity transactionEntity = new TransactionEntity(userEntity);
            transactionEntity.setTid(String.valueOf(mongoDBService.generateSequence("transactions_sequence")));
            transactionEntity = transactionRepository.save(transactionEntity);

            List<TransactionProductEntity> transactionProductEntityList = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;
            for (CartItemEntity cartItemEntity : cartItemList) {
                TransactionProductEntity transactionProductEntity = transactionProductService.createTransactionProduct(cartItemEntity, transactionEntity);
                transactionProductEntityList.add(transactionProductEntity);
                total = total.add(
                        BigDecimal.valueOf(transactionProductEntity.getQuantity())
                                .multiply(transactionProductEntity.getPrice())
                );
                transactionEntity.setTotal(total);
                transactionEntity = transactionRepository.save(transactionEntity);
            }
            return new TransactionResponseData(transactionEntity, transactionProductEntityList);
        } catch (TransactionPreparationException ex) {
            logger.warn(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.warn("Prepare transaction failed: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public TransactionResponseData getTransactionById(FirebaseUserData firebaseUserData, String tid) {
        try {
            TransactionEntity transactionEntity = getTransactionEntityById(firebaseUserData, tid);
            List<TransactionProductEntity> transactionProductEntityList = transactionProductService.getAllTransactionProductsByTransation(transactionEntity);
            return new TransactionResponseData(transactionEntity, transactionProductEntityList);
        } catch (TransactionPreparationException ex) {
            logger.warn("Get transaction failed: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public String payTransaction(FirebaseUserData firebaseUserData, String tid) {
        try {
            TransactionEntity transactionEntity = getTransactionEntityById(firebaseUserData, tid);
            validateTransactionPreparingStatus(transactionEntity);

            List<TransactionProductEntity> transactionProductEntityList = transactionProductService.getAllTransactionProductsByTransation(transactionEntity);
            processStockDeduction(transactionProductEntityList);

            transactionEntity.setStatus(TransactionStatus.PROCESSING);
            Map<String, String> response = stripeService.createStripeSession(transactionProductEntityList, transactionEntity.getTid());
            transactionEntity.setSessionId(response.get("sessionId"));
            transactionRepository.save(transactionEntity);
            return response.get("url");
        } catch (TransactionStatusException ex) {
            logger.warn("Pay transaction failed due to business logic: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Pay transaction failed: {}", ex.getMessage());
            throw new RuntimeException("An unexpected error occurred while processing the payment.", ex);
        }
    }

    @Override
    public TransactionResponseData finishTransaction(FirebaseUserData firebaseUserData, String tid) {
        try {
            TransactionEntity transactionEntity = getTransactionEntityById(firebaseUserData, tid);
            if (!stripeService.getPaymentStatusBySessionId(transactionEntity.getSessionId()).equals("complete")){
                throw new TransactionStatusException("Transaction not completed on stripe session");
            }
            validateTransactionProcessingStatus(transactionEntity);
            UserEntity userEntity = userService.getEntityByFirebaseUserData(firebaseUserData);
            cartItemService.emptyCartItem(transactionEntity, userEntity);
            transactionEntity.setStatus(TransactionStatus.SUCCESS);
            transactionRepository.save(transactionEntity);
            List<TransactionProductEntity> transactionProductEntityList = transactionProductService.getAllTransactionProductsByTransation(transactionEntity);
            return new TransactionResponseData(transactionEntity, transactionProductEntityList);
        } catch (TransactionStatusException ex) {
            logger.warn("Finish transaction failed due to business logic: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Finish transaction failed: {}", ex.getMessage());
            throw new RuntimeException("An unexpected error occurred while finishing the payment.", ex);
        }
    }

    //Helper method
    public TransactionEntity getTransactionEntityById(FirebaseUserData firebaseUserData, String tid) {
        UserEntity userEntity = userService.getEntityByFirebaseUserData(firebaseUserData);
        return transactionRepository.findByUserAndTid(userEntity, tid).orElseThrow(
                () -> new TransactionNotFoundException("Transaction not found for tid: " + tid)
        );
    }

    private void validateTransactionPreparingStatus(TransactionEntity transactionEntity) {
        if (!transactionEntity.getStatus().equals(TransactionStatus.PREPARE)) {
            throw new TransactionStatusException("Invalid transaction status: " + transactionEntity.getStatus());
        }
    }

    private void validateTransactionProcessingStatus(TransactionEntity transactionEntity) {
        if (!transactionEntity.getStatus().equals(TransactionStatus.PROCESSING)) {
            throw new TransactionStatusException("Invalid transaction status: " + transactionEntity.getStatus());
        }
    }

    private void processStockDeduction(List<TransactionProductEntity> transactionProductEntityList) {
        for (TransactionProductEntity transactionProductEntity : transactionProductEntityList) {
            productService.deduceStock(transactionProductEntity);
        }
    }

}
