package com.fsse2406.project.data.product.domainObject;

import com.fsse2406.project.data.product.entity.ProductEntity;
import com.fsse2406.project.data.stripe.domainObject.StripeResponseData;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponseData {
    private String objectId;
    private String pid;
    private String name;
    private String summary;
    private List<String> images;
    private List<String> descrImages;
    private BigDecimal price;
    private Integer stock;
    private StripeResponseData stripeDetails;
    private Boolean isPopularPicks;
    private String category;

    public ProductResponseData(ProductEntity productEntity) {
        this.pid = productEntity.getPid();
        this.name = productEntity.getName();
        this.summary = productEntity.getSummary();
        this.images = productEntity.getImages();
        this.descrImages = productEntity.getDescrImages();
        this.price = productEntity.getPrice();
        this.stock = productEntity.getStock();
        this.stripeDetails = productEntity.getStripeDetails() != null
                ? new StripeResponseData(productEntity.getStripeDetails())
                : null;
        this.isPopularPicks = productEntity.getIsPopularPicks();
        this.category = productEntity.getCategory();
    }
}