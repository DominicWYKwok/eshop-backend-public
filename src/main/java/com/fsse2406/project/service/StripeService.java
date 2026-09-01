package com.fsse2406.project.service;


import com.fsse2406.project.data.stripe.domainObject.CreatePriceResponseData;
import com.fsse2406.project.data.stripe.domainObject.CreateProductResponseData;
import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;
import com.stripe.exception.StripeException;

import java.util.List;
import java.util.Map;

public interface StripeService {

    CreateProductResponseData createProduct(String pid) throws StripeException;

    void createAllProduct() throws StripeException;

    void createAllPrices() throws StripeException;

    Map<String, String> createStripeSession(List<TransactionProductEntity> transactionProductEntityList, String tid) throws StripeException;

    CreatePriceResponseData createPrice(String pid) throws StripeException;

    String getPaymentStatusBySessionId(String sessionId) throws StripeException;

    void getEntity(String pid);
}