package com.fsse2406.project.service.impl;

import com.fsse2406.project.data.cart.domainObject.CartItemResponseData;
import com.fsse2406.project.data.cart.entity.CartItemEntity;
import com.fsse2406.project.data.product.entity.ProductEntity;
import com.fsse2406.project.data.transaction.entity.TransactionEntity;
import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import com.fsse2406.project.data.user.entity.UserEntity;
import com.fsse2406.project.exception.cartItem.CartItemException;
import com.fsse2406.project.repository.CartItemRepository;
import com.fsse2406.project.service.CartItemService;
import com.fsse2406.project.service.MongoDBService;
import com.fsse2406.project.service.ProductService;
import com.fsse2406.project.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;


@Service
public class CartItemServiceImpl implements CartItemService {

    private static final Logger logger = LoggerFactory.getLogger(CartItemServiceImpl.class);
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ProductService productService;
    private final MongoDBService mongoDBService;

    public CartItemServiceImpl(UserService userService, CartItemRepository cartItemRepository, ProductService productService, MongoDBService mongoDBService) {
        this.userService = userService;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
        this.mongoDBService = mongoDBService;
    }

    @Override
    public boolean putCartItem(String pid, Integer quantity, FirebaseUserData firebaseUserData) {
        try {
            UserEntity userEntity = userService.getEntityByFirebaseUserData(firebaseUserData);
            ProductEntity productEntity = productService.getProductEntityByPid(pid);
            validateQuantityGreaterThanZero(quantity);
            Optional<CartItemEntity> optionalCartItemEntity = cartItemRepository.findByProductAndUser(productEntity, userEntity);
            if (optionalCartItemEntity.isEmpty()) {
                validateQuantitySmallerThanStock(quantity, productEntity.getStock());
                CartItemEntity cartItem = new CartItemEntity(productEntity, userEntity, quantity);
                cartItem.setCid(String.valueOf(mongoDBService.generateSequence("cart_item_sequence")));
                cartItemRepository.save(cartItem);
            } else {
                CartItemEntity cartItemEntity = optionalCartItemEntity.get();
                System.out.println(cartItemEntity.getQuantity());
                int newQuantity = cartItemEntity.getQuantity() + quantity;
                validateQuantitySmallerThanStock(newQuantity, productEntity.getStock());
                cartItemEntity.setQuantity(newQuantity);
                cartItemRepository.save(cartItemEntity);
                System.out.println(cartItemEntity.getQuantity());
            }
            return true;
        } catch (CartItemException ex) {
            logger.warn(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.warn("Put cart item failed{}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<CartItemResponseData> getAllCartItems(FirebaseUserData firebaseUserData) {
        List<CartItemResponseData> cartItemResponseDataList = new ArrayList<>();
        UserEntity userEntity = userService.getEntityByFirebaseUserData(firebaseUserData);
        for (CartItemEntity cartItemEntity : cartItemRepository.findAllByUser(userEntity)) {
            cartItemResponseDataList.add(new CartItemResponseData(cartItemEntity));
        }
        return cartItemResponseDataList;
    }

    @Override
    public CartItemResponseData updateCartItem(String pid, Integer quantity, FirebaseUserData firebaseUserData) {
        try {
            validateQuantityGreaterThanZero(quantity);
            ProductEntity productEntity = productService.getProductEntityByPid(pid);
            UserEntity userEntity = userService.getEntityByFirebaseUserData(firebaseUserData);
            CartItemEntity cartItemEntity = getCartItemEntityByProductAndUser(productEntity, userEntity);
            validateQuantitySmallerThanStock(quantity, productService.getProductEntityByPid(pid).getStock());
            cartItemEntity.setQuantity(quantity);
            cartItemRepository.save(cartItemEntity);
            return new CartItemResponseData(cartItemEntity);
        } catch (CartItemException ex) {
            logger.warn(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.warn("Cart update failed{}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public boolean deleteCartItem(String pid, FirebaseUserData firebaseUserData) {
        try {
            CartItemEntity cartItemEntity = getCartItemEntityByProductAndUser(
                    productService.getProductEntityByPid(pid),
                    userService.getEntityByFirebaseUserData(firebaseUserData)
            );
            cartItemRepository.delete(cartItemEntity);
            return true;
        } catch (Exception ex) {
            logger.warn("Delete cart item failed: {}", ex.getMessage());
            throw ex;
        }
    }

    public CartItemEntity getCartItemEntityByProductAndUser(ProductEntity productEntity, UserEntity userEntity) {
        return cartItemRepository.findByProductAndUser(productEntity, userEntity).orElseThrow(
                () -> new CartItemException("Cart not found.")
        );
    }


    private void validateQuantitySmallerThanStock(Integer quantity, Integer stock) {
        if (quantity > stock) {
            throw new CartItemException("quantity must be smaller than stock");
        }
    }

    private void validateQuantityGreaterThanZero(Integer quantity) {
        if (quantity <= 0) {
            throw new CartItemException("quantity must be greater than zero");
        }
    }

    @Override
    public List<CartItemEntity> getCartItemListByUser(UserEntity user) {
        return new ArrayList<>(cartItemRepository.findAllByUser(user));
    }

    @Override
    public void emptyCartItem(TransactionEntity transactionEntity, UserEntity user) {
        cartItemRepository.deleteAllByUser(user);
    }

}
