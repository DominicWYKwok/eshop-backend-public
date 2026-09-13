package com.fsse2406.project.service.impl;

import com.fsse2406.project.data.cart.entity.CartItemEntity;
import com.fsse2406.project.data.product.entity.ProductEntity;
import com.fsse2406.project.data.user.domainObject.FirebaseUserData;
import com.fsse2406.project.data.user.entity.UserEntity;
import com.fsse2406.project.exception.cartItem.CartItemException;
import com.fsse2406.project.repository.CartItemRepository;
import com.fsse2406.project.service.MongoDBService;
import com.fsse2406.project.service.ProductService;
import com.fsse2406.project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartItemServiceImplTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private MongoDBService mongoDBService;

    @Mock
    private FirebaseUserData firebaseUserData;

    private CartItemServiceImpl cartItemService;
    private ProductEntity product;
    private UserEntity user;

    @BeforeEach
    void setUp() {
        cartItemService = new CartItemServiceImpl(
                userService,
                cartItemRepository,
                productService,
                mongoDBService
        );

        product = new ProductEntity();
        product.setPid("p-1");
        product.setStock(10);

        user = new UserEntity();
        user.setFirebaseUid("user-1");

        lenient().when(userService.getEntityByFirebaseUserData(firebaseUserData)).thenReturn(user);
        lenient().when(productService.getProductEntityByPid("p-1")).thenReturn(product);
    }

    @Test
    void putCartItemCreatesNewItemWhenQuantityIsWithinStock() {
        when(cartItemRepository.findByProductAndUser(product, user))
                .thenReturn(Optional.empty());
        when(mongoDBService.generateSequence("cart_item_sequence"))
                .thenReturn(7L);

        boolean result = cartItemService.putCartItem("p-1", 3, firebaseUserData);

        assertEquals(true, result);
        ArgumentCaptor<CartItemEntity> captor = ArgumentCaptor.forClass(CartItemEntity.class);
        verify(cartItemRepository).save(captor.capture());
        assertEquals("p-1", captor.getValue().getProduct().getPid());
        assertEquals(3, captor.getValue().getQuantity());
        assertEquals("7", captor.getValue().getCid());
    }

    @Test
    void putCartItemRejectsExistingItemWhenNewQuantityExceedsStock() {
        CartItemEntity existingItem = new CartItemEntity(product, user, 8);
        when(cartItemRepository.findByProductAndUser(product, user))
                .thenReturn(Optional.of(existingItem));

        assertThrows(
                CartItemException.class,
                () -> cartItemService.putCartItem("p-1", 3, firebaseUserData)
        );

        assertEquals(8, existingItem.getQuantity());
        verify(cartItemRepository, never()).save(any(CartItemEntity.class));
    }

    @Test
    void putCartItemRejectsNonPositiveQuantity() {
        assertThrows(
                CartItemException.class,
                () -> cartItemService.putCartItem("p-1", 0, firebaseUserData)
        );

        verify(cartItemRepository, never()).save(any(CartItemEntity.class));
    }

    @Test
    void putCartItemAddsQuantityToExistingItemWhenWithinStock() {
        CartItemEntity existingItem = new CartItemEntity(product, user, 4);
        when(cartItemRepository.findByProductAndUser(product, user))
                .thenReturn(Optional.of(existingItem));

        boolean result = cartItemService.putCartItem("p-1", 3, firebaseUserData);

        assertTrue(result);
        assertEquals(7, existingItem.getQuantity());
        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void putCartItemRejectsNewItemWhenQuantityExceedsStock() {
        when(cartItemRepository.findByProductAndUser(product, user))
                .thenReturn(Optional.empty());

        assertThrows(CartItemException.class,
                () -> cartItemService.putCartItem("p-1", 11, firebaseUserData));

        verify(cartItemRepository, never()).save(any(CartItemEntity.class));
        verify(mongoDBService, never()).generateSequence("cart_item_sequence");
    }

    @Test
    void putCartItemRejectsNegativeQuantity() {
        assertThrows(CartItemException.class,
                () -> cartItemService.putCartItem("p-1", -1, firebaseUserData));

        verify(cartItemRepository, never()).save(any(CartItemEntity.class));
    }

    @Test
    void updateCartItemUpdatesExistingItemWhenQuantityIsWithinStock() {
        CartItemEntity existingItem = new CartItemEntity(product, user, 2);
        when(cartItemRepository.findByProductAndUser(product, user))
                .thenReturn(Optional.of(existingItem));

        var result = cartItemService.updateCartItem("p-1", 6, firebaseUserData);

        assertEquals(6, result.getQuantity());
        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void updateCartItemRejectsQuantityExceedingStock() {
        CartItemEntity existingItem = new CartItemEntity(product, user, 2);
        when(cartItemRepository.findByProductAndUser(product, user))
                .thenReturn(Optional.of(existingItem));

        assertThrows(CartItemException.class,
                () -> cartItemService.updateCartItem("p-1", 11, firebaseUserData));

        assertEquals(2, existingItem.getQuantity());
        verify(cartItemRepository, never()).save(any(CartItemEntity.class));
    }

    @Test
    void getAllCartItemsMapsRepositoryResults() {
        CartItemEntity item = new CartItemEntity(product, user, 3);
        item.setCid("7");
        when(cartItemRepository.findAllByUser(user)).thenReturn(List.of(item));

        var result = cartItemService.getAllCartItems(firebaseUserData);

        assertEquals(1, result.size());
        assertEquals("7", result.get(0).getCid());
        assertEquals("p-1", result.get(0).getProduct().getPid());
        assertEquals(3, result.get(0).getQuantity());
    }

    @Test
    void deleteCartItemDeletesExistingItem() {
        CartItemEntity item = new CartItemEntity(product, user, 2);
        when(cartItemRepository.findByProductAndUser(product, user))
                .thenReturn(Optional.of(item));

        assertTrue(cartItemService.deleteCartItem("p-1", firebaseUserData));

        verify(cartItemRepository).delete(item);
    }

    @Test
    void deleteCartItemThrowsWhenItemDoesNotExist() {
        when(cartItemRepository.findByProductAndUser(product, user))
                .thenReturn(Optional.empty());

        assertThrows(CartItemException.class,
                () -> cartItemService.deleteCartItem("p-1", firebaseUserData));

        verify(cartItemRepository, never()).delete(any(CartItemEntity.class));
    }

    @Test
    void emptyCartItemDeletesAllItemsForUser() {
        cartItemService.emptyCartItem(null, user);

        verify(cartItemRepository).deleteAllByUser(user);
    }
}
