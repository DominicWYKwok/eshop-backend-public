package com.fsse2406.project.service;

import com.fsse2406.project.data.transaction.domainObject.TransactionResponseData;
import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;
import com.fsse2406.project.data.user.domainObject.FirebaseUserData;

import java.util.List;

public interface TransactionService{
    TransactionResponseData prepareTransaction(FirebaseUserData firebaseUserData);
    TransactionResponseData getTransactionById(FirebaseUserData firebaseUserData, String tid);
    String payTransaction(FirebaseUserData firebaseUserData, String tid);
    TransactionResponseData finishTransaction(FirebaseUserData firebaseUserData, String tid);
}
