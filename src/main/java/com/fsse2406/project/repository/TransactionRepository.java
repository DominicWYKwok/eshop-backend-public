package com.fsse2406.project.repository;

import com.fsse2406.project.data.transaction.entity.TransactionEntity;
import com.fsse2406.project.data.user.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends MongoRepository<TransactionEntity, String> {
    Optional<TransactionEntity> findByUserAndTid(UserEntity user, String tid);
}
