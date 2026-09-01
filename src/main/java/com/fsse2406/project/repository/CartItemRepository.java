package com.fsse2406.project.repository;

import com.fsse2406.project.data.cart.entity.CartItemEntity;
import com.fsse2406.project.data.product.entity.ProductEntity;
import com.fsse2406.project.data.user.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends MongoRepository<CartItemEntity, String> {
    Optional<CartItemEntity> findByProductAndUser(ProductEntity product, UserEntity user);
    List<CartItemEntity> findAllByUser(UserEntity user);
    void deleteAllByUser(UserEntity user);
}
