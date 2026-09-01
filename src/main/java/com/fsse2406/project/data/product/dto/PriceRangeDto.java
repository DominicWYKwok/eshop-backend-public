package com.fsse2406.project.data.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceRangeDto {
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public PriceRangeDto(BigDecimal minPrice, BigDecimal maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}
