package com.fsse2406.project.data.transaction.entity;

import com.fsse2406.project.data.cart.status.TransactionStatus;
import com.fsse2406.project.data.user.entity.UserEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "transactions")  // MongoDB collection name
public class TransactionEntity {
    @Id
    private ObjectId transactionObjectId;

    private String tid;

    private String sessionId;

    @DBRef
    private UserEntity user;

    private LocalDateTime datetime;

    private TransactionStatus status;

    private BigDecimal total;

    public TransactionEntity(UserEntity user) {
        this.user = user;
        this.datetime = LocalDateTime.now();
        this.status = TransactionStatus.PREPARE;
        this.total = BigDecimal.ZERO;
    }
}
