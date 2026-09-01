package com.fsse2406.project.repository;

import com.fsse2406.project.data.stripe.entity.StripeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StripeRepository extends MongoRepository<StripeEntity, String> {
}