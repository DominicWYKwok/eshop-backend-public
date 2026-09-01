package com.fsse2406.project.exception.product;

import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StockDeductionException extends RuntimeException  {
    public StockDeductionException(Integer stock, TransactionProductEntity transactionProductEntity) {
        super("Insufficient stock for product ID " + transactionProductEntity.getPid() +
                ". Requested: " + transactionProductEntity.getQuantity() +
                ", Available: " + stock);
    }
}
