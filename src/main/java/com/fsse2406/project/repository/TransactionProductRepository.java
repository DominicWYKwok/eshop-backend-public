package com.fsse2406.project.repository;

import com.fsse2406.project.data.transaction.entity.TransactionEntity;
import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionProductRepository extends MongoRepository<TransactionProductEntity, String> {
    List<TransactionProductEntity> findAllByTransaction(TransactionEntity transaction);
}
