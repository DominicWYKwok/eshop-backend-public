package com.fsse2406.project.data.product.dto;

import com.fsse2406.project.data.product.domainObject.ProductResponseData;
import com.fsse2406.project.data.stripe.dto.StripeResponseDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductResponseDto {
    private String pid;
    private String name;
    private List<String> images;
    private BigDecimal price;
    private Boolean hasStock;
    private StripeResponseDto stripeDetails;
    private Boolean isPopularPicks;
    private String category;

    public ProductResponseDto(ProductResponseData productResponseData) {
        this.pid = productResponseData.getPid();
        this.name = productResponseData.getName();
        this.images = productResponseData.getImages();
        this.price = productResponseData.getPrice();
        this.hasStock = productResponseData.getStock() > 0;
        this.stripeDetails = productResponseData.getStripeDetails() != null
                ? new StripeResponseDto(productResponseData.getStripeDetails())
                : null;
        this.isPopularPicks = productResponseData.getIsPopularPicks();
        this.category = productResponseData.getCategory();
    }
}
