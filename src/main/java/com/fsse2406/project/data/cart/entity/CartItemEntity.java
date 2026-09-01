package com.fsse2406.project.data.cart.entity;

import com.fsse2406.project.data.product.entity.ProductEntity;
import com.fsse2406.project.data.user.entity.UserEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "cart_items")
public class CartItemEntity {
    @Id
    private String cartObjectId;

    private String cid;

    @DBRef
    private ProductEntity product;

    @DBRef
    private UserEntity user;

    private Integer quantity;

    public CartItemEntity(ProductEntity productEntity, UserEntity userEntity, Integer quantity) {
        this.product = productEntity;
        this.user = userEntity;
        this.quantity = quantity;
    }
}
