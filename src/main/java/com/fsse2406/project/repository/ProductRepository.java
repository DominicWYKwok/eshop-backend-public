package com.fsse2406.project.repository;

import com.fsse2406.project.data.product.entity.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends MongoRepository<ProductEntity, String> {
    List<ProductEntity> findByNameContaining(String param);
    Optional<ProductEntity> findByPid(String pid);
    ProductEntity findTopByOrderByPriceAsc();
    ProductEntity findTopByOrderByPriceDesc();
}
