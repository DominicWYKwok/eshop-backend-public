package com.fsse2406.project.data.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fsse2406.project.data.cart.status.TransactionStatus;
import com.fsse2406.project.data.transaction.domainObject.TransactionResponseData;
import com.fsse2406.project.data.transactionProduct.domainObject.TransactionProductResponseData;
import com.fsse2406.project.data.transactionProduct.dto.TransactionProductResponseDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonPropertyOrder({"tid", "buyer_uid", "datetime", "status", "total", "items"})
public class TransactionResponseDto {
    private String tid;
    @JsonProperty("buyer_uid")
    private String user;
    @JsonFormat(pattern = "yyyyMMdd'T'HH:mm:ss")
    private LocalDateTime datetime;
    private TransactionStatus status;
    private BigDecimal total;
    List<TransactionProductResponseDto> items = new ArrayList<>();

    public TransactionResponseDto(TransactionResponseData transactionResponseData) {
        this.tid = transactionResponseData.getTid();
        this.user = transactionResponseData.getUser().getUid();
        this.datetime = transactionResponseData.getDatetime();
        this.status = transactionResponseData.getStatus();
        this.total = transactionResponseData.getTotal();
        setItems(transactionResponseData);
    }

    public void setItems(TransactionResponseData data) {
        for (TransactionProductResponseData transactionProductResponseData : data.getTransactionProducts()) {
            this.items.add(
                    new TransactionProductResponseDto(transactionProductResponseData));
        }
    }
}
