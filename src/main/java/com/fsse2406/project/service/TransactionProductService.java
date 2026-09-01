package com.fsse2406.project.service;

import com.fsse2406.project.data.cart.entity.CartItemEntity;
import com.fsse2406.project.data.transaction.entity.TransactionEntity;
import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;

import java.util.List;

public interface TransactionProductService {
    TransactionProductEntity createTransactionProduct(CartItemEntity cartItemEntity, TransactionEntity transactionEntity);

    List<TransactionProductEntity> getAllTransactionProductsByTransation(TransactionEntity transactionEntity);
}
