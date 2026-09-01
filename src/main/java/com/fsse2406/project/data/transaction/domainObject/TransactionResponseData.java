package com.fsse2406.project.data.transaction.domainObject;

import com.fsse2406.project.data.cart.status.TransactionStatus;
import com.fsse2406.project.data.transaction.entity.TransactionEntity;
import com.fsse2406.project.data.transactionProduct.domainObject.TransactionProductResponseData;
import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;
import com.fsse2406.project.data.user.domainObject.response.UserResponseData;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionResponseData {
    private String tid;
    private UserResponseData user;
    private LocalDateTime datetime;
    private TransactionStatus status;
    private BigDecimal total;
    List<TransactionProductResponseData> transactionProducts = new ArrayList<>();

    public TransactionResponseData(TransactionEntity transactionEntity, List<TransactionProductEntity> transactionProductEntity) {
        this.tid = transactionEntity.getTid();
        this.user = new UserResponseData(transactionEntity.getUser());
        this.datetime = transactionEntity.getDatetime();
        this.status = transactionEntity.getStatus();
        this.total = transactionEntity.getTotal();
        setTransactionProducts(transactionProductEntity);
    }

    public void setTransactionProducts(List<TransactionProductEntity> entity) {
        for (TransactionProductEntity transactionProductEntity : entity) {
            this.transactionProducts.add(
                    new TransactionProductResponseData(transactionProductEntity));
        }
    }
}
