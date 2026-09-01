package com.fsse2406.project.service;

import com.fsse2406.project.data.cart.domainObject.CartItemResponseData;
import com.fsse2406.project.data.cart.entity.CartItemEntity;
import com.fsse2406.project.data.transaction.entity.TransactionEntity;
import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import com.fsse2406.project.data.user.entity.UserEntity;

import java.util.List;

public interface CartItemService {
    boolean putCartItem(String pid, Integer quantity, FirebaseUserData firebaseUserData);
    List<CartItemResponseData> getAllCartItems(FirebaseUserData firebaseUserData);

    CartItemResponseData updateCartItem(String pid, Integer quantity, FirebaseUserData firebaseUserData);

    boolean deleteCartItem(String pid, FirebaseUserData firebaseUserData);

    List<CartItemEntity> getCartItemListByUser(UserEntity user);

    void emptyCartItem(TransactionEntity transactionEntity, UserEntity user);

}