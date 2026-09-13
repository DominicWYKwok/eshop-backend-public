package com.fsse2406.project.repository;

import com.fsse2406.project.data.product.entity.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@Testcontainers
@Tag("integration")
class ProductRepositoryIntegrationTest {

    @Container
    static final MongoDBContainer MONGODB = new MongoDBContainer(
            DockerImageName.parse("mongo:7.0")
    );

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", MONGODB::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "eshop_test");
    }

    @BeforeEach
    void cleanDatabase() {
        productRepository.deleteAll();
    }

    @Test
    void savesAndFindsProductByPid() {
        ProductEntity product = new ProductEntity();
        product.setPid("p-1");
        product.setName("Coffee mug");
        product.setPrice(new BigDecimal("48.00"));
        product.setStock(4);

        productRepository.save(product);

        assertTrue(productRepository.findByPid("p-1").isPresent());
        assertEquals(
                "Coffee mug",
                productRepository.findByPid("p-1").orElseThrow().getName()
        );
    }
}
