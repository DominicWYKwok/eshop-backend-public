package com.fsse2406.project.repository;

import com.fsse2406.project.data.cart.status.TransactionStatus;
import com.fsse2406.project.data.transaction.entity.TransactionEntity;
import com.fsse2406.project.data.user.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends MongoRepository<TransactionEntity, String> {
    Optional<TransactionEntity> findByUserAndTid(UserEntity user, String tid);

    List<TransactionEntity> findByUserAndStatusOrderByDatetimeDesc(UserEntity user, TransactionStatus status);
}
