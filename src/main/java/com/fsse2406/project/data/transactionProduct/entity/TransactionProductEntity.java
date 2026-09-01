package com.fsse2406.project.data.transactionProduct.entity;

import com.fsse2406.project.data.cart.entity.CartItemEntity;
import com.fsse2406.project.data.stripe.entity.StripeEntity;
import com.fsse2406.project.data.transaction.entity.TransactionEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "transaction_products")
public class TransactionProductEntity {
    @Id
    private String tpid;

    @DBRef
    private TransactionEntity transaction;

    private ObjectId transactionId;
    private String pid;
    private String name;
    private String summary;
    private BigDecimal price;
    private Integer stock;
    private Integer quantity;
    private List<String> images;
    private StripeEntity stripeDetails;
    private String category;

    public TransactionProductEntity(TransactionEntity transactionEntity, CartItemEntity cartItemEntity) {
        this.transaction = transactionEntity;
        this.transactionId = transactionEntity.getTransactionObjectId();
        this.pid = cartItemEntity.getProduct().getPid();
        this.name = cartItemEntity.getProduct().getName();
        this.summary = cartItemEntity.getProduct().getSummary();
        this.price = cartItemEntity.getProduct().getPrice();
        this.stock = cartItemEntity.getProduct().getStock();
        this.quantity = cartItemEntity.getQuantity();
        this.images = cartItemEntity.getProduct().getImages();
        this.stripeDetails = cartItemEntity.getProduct().getStripeDetails();
        this.category = cartItemEntity.getProduct().getCategory();
    }
}
