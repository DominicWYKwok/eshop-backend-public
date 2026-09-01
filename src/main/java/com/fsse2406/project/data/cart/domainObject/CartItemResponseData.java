package com.fsse2406.project.data.cart.domainObject;

import com.fsse2406.project.data.cart.entity.CartItemEntity;
import com.fsse2406.project.data.product.domainObject.ProductResponseData;
import com.fsse2406.project.data.user.domainObject.response.UserResponseData;
import lombok.Data;

@Data
public class CartItemResponseData {
    private String cid;
    private ProductResponseData product;
    private UserResponseData user;
    private Integer quantity;

    public CartItemResponseData(CartItemEntity cartItemEntity) {
        this.cid = cartItemEntity.getCid();
        this.product = new ProductResponseData(cartItemEntity.getProduct());
        this.user = new UserResponseData(cartItemEntity.getUser());
        this.quantity = cartItemEntity.getQuantity();
    }
}


