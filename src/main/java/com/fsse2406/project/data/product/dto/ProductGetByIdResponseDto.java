package com.fsse2406.project.data.product.dto;

import com.fsse2406.project.data.product.domainObject.ProductResponseData;
import com.fsse2406.project.data.stripe.dto.StripeResponseDto;
import com.fsse2406.project.data.transactionProduct.domainObject.TransactionProductResponseData;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductGetByIdResponseDto {
    private String pid;
    private String name;
    private String summary;
    private List<String> images;
    private List<String> descrImages;
    private BigDecimal price;
    private Integer stock;
    private StripeResponseDto stripeDetails;
    private String category;

    public ProductGetByIdResponseDto(ProductResponseData productResponseData) {
        this.pid = productResponseData.getPid();
        this.name = productResponseData.getName();
        this.summary = productResponseData.getSummary();
        this.images = productResponseData.getImages();
        this.descrImages = productResponseData.getDescrImages();
        this.price = productResponseData.getPrice();
        this.stock = productResponseData.getStock();
        this.stripeDetails = productResponseData.getStripeDetails() != null
                ? new StripeResponseDto(productResponseData.getStripeDetails())
                : null;
        this.category = productResponseData.getCategory();
    }

    public ProductGetByIdResponseDto(TransactionProductResponseData transactionProductResponseData) {
        this.pid = transactionProductResponseData.getPid();
        this.name = transactionProductResponseData.getName();
        this.summary = transactionProductResponseData.getSummary();
        this.images = transactionProductResponseData.getImages();
        this.price = transactionProductResponseData.getPrice();
        this.stock = transactionProductResponseData.getStock();
        this.stripeDetails = transactionProductResponseData.getStripeDetails() != null
                ? new StripeResponseDto(transactionProductResponseData.getStripeDetails())
                : null;
        this.category = transactionProductResponseData.getCategory();
    }
}
