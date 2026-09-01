package com.fsse2406.project.data.transactionProduct.domainObject;

import com.fsse2406.project.data.stripe.domainObject.StripeResponseData;
import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TransactionProductResponseData {
    private String tpid;
    private String tid;
    private String pid;
    private String name;
    private String summary;
    private List<String> images;
    private BigDecimal price;
    private Integer stock;
    private Integer quantity;
    private StripeResponseData stripeDetails;
    private String category;

    public TransactionProductResponseData(TransactionProductEntity transactionProductEntity) {
        this.tid = transactionProductEntity.getTransaction().getTid();
        this.tpid = transactionProductEntity.getTpid();
        this.pid = transactionProductEntity.getPid();
        this.name = transactionProductEntity.getName();
        this.summary = transactionProductEntity.getSummary();
        this.images = transactionProductEntity.getImages();
        this.price = transactionProductEntity.getPrice();
        this.stock = transactionProductEntity.getStock();
        this.quantity = transactionProductEntity.getQuantity();
        this.stripeDetails = transactionProductEntity.getStripeDetails() != null
                ? new StripeResponseData(transactionProductEntity.getStripeDetails())
                : null;
        this.category = transactionProductEntity.getCategory();
    }
}