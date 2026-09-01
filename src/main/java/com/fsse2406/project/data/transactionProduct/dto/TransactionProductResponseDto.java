package com.fsse2406.project.data.transactionProduct.dto;

import com.fsse2406.project.data.product.dto.ProductGetByIdResponseDto;
import com.fsse2406.project.data.transactionProduct.domainObject.TransactionProductResponseData;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionProductResponseDto {
    private ProductGetByIdResponseDto product;
    private Integer quantity;
    private BigDecimal subtotal;

    public TransactionProductResponseDto(TransactionProductResponseData transactionProductResponseData) {

        this.product = new ProductGetByIdResponseDto(transactionProductResponseData);
        this.quantity = transactionProductResponseData.getQuantity();
        this.subtotal = transactionProductResponseData.getPrice().multiply(
                BigDecimal.valueOf(transactionProductResponseData.getQuantity())
        );
    }
}