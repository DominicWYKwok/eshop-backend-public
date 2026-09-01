package com.fsse2406.project.service.impl;

import com.fsse2406.project.data.cart.entity.CartItemEntity;
import com.fsse2406.project.data.transaction.entity.TransactionEntity;
import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;
import com.fsse2406.project.repository.TransactionProductRepository;
import com.fsse2406.project.service.TransactionProductService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionProductServiceImpl implements TransactionProductService {

    private final TransactionProductRepository transactionProductRepository;

    public TransactionProductServiceImpl(TransactionProductRepository transactionProductRepository) {
        this.transactionProductRepository = transactionProductRepository;
    }

    @Override
    public TransactionProductEntity createTransactionProduct(CartItemEntity cartItemEntity, TransactionEntity transactionEntity) {
        TransactionProductEntity transactionProductEntity = new TransactionProductEntity(transactionEntity, cartItemEntity);
//        List<ProductImagesEntity> productImages = cartItemEntity.getProduct().getImages();
//        List<TransactionProductImageEntity> transactionProductImages = new ArrayList<>();
//
//        for (ProductImagesEntity productImage : productImages) {
//            TransactionProductImageEntity transactionProductImage = new TransactionProductImageEntity();
//            transactionProductImage.setImageUrl(productImage.getImageUrl());
//            transactionProductImage.setTransactionProduct(transactionProductEntity);
//            transactionProductImages.add(transactionProductImage);
//        }
//
//        transactionProductEntity.setImages(transactionProductImages);
        transactionProductRepository.save(transactionProductEntity);
        return transactionProductEntity;
    }

    @Override
    public List<TransactionProductEntity> getAllTransactionProductsByTransation(TransactionEntity transactionEntity) {
        return new ArrayList<>(transactionProductRepository.findAllByTransaction(transactionEntity));
    }
}
