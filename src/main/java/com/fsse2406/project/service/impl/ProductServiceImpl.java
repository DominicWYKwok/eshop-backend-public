package com.fsse2406.project.service.impl;

import com.fsse2406.project.data.product.domainObject.ProductResponseData;
import com.fsse2406.project.data.product.dto.PriceRangeDto;
import com.fsse2406.project.data.product.entity.ProductEntity;
import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;
import com.fsse2406.project.exception.product.ProductNotFoundException;
import com.fsse2406.project.exception.product.StockDeductionException;
import com.fsse2406.project.repository.ProductRepository;
import com.fsse2406.project.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;

    public ProductServiceImpl(ProductRepository productRepository, MongoTemplate mongoTemplate) {
        this.productRepository = productRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Fetch products with optional filters and sorting.
     *
     * @param sortBy     e.g. "price-ascending", "alpha-descending", etc.
     * @param categories optional list of categories to include
     * @param minPrice   optional minimum price (inclusive)
     * @param maxPrice   optional maximum price (inclusive)
     */

    //Get data/entity
    @Override
    public List<ProductResponseData> getAllProducts(List<String> categories,
                                                    Double minPrice,
                                                    Double maxPrice,
                                                    String sortBy) {
        Query query = new Query();

        // 1) Filtering criteria
        if (categories != null && !categories.isEmpty()) {
            query.addCriteria(Criteria.where("category").in(categories));
        }
        if (minPrice != null && maxPrice != null) {
            query.addCriteria(Criteria.where("price").gte(minPrice).lte(maxPrice));
        } else if (minPrice != null) {
            query.addCriteria(Criteria.where("price").gte(minPrice));
        } else if (maxPrice != null) {
            query.addCriteria(Criteria.where("price").lte(maxPrice));
        }

        if (sortBy != null && !sortBy.isEmpty() && !"pid".equals(sortBy)) {
            query.with(buildSort(sortBy));
            // direct DB sort
            return mongoTemplate.find(query, ProductEntity.class).stream()
                    .map(ProductResponseData::new)
                    .toList();
        }

        // 3) Default / pid-based sort
        // remove any sort on query, fetch all matching
        List<ProductEntity> results = mongoTemplate.find(query, ProductEntity.class);
        // then sort by pid numerically in Java
        return results.stream()
                .sorted(Comparator.comparingInt(p -> Integer.parseInt(p.getPid())))
                .map(ProductResponseData::new)
                .toList();
    }


    @Override
    public ProductResponseData getProductDataById(String pid) {
        try {
            ProductEntity productEntity = productRepository.findByPid(pid).orElseThrow(
                    () -> new ProductNotFoundException(pid)
            );
            return new ProductResponseData(productEntity);
        } catch (ProductNotFoundException ex) {
            logger.warn("getProductDataById method. {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<ProductEntity> getAllProductEntity() {
        return new ArrayList<>(productRepository.findAll());
    }

    @Override
    public ProductEntity getProductEntityByPid(String pid) {
        try {
            return productRepository.findByPid(pid).orElseThrow(
                    () -> new ProductNotFoundException(pid)
            );
        } catch (ProductNotFoundException ex) {
            logger.warn("getProductEntityById method. {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<ProductResponseData> getAllProductByName(String name) {
        List<ProductEntity> productEntities = productRepository.findByNameContaining(name);
        return productEntities.stream()
                .map(ProductResponseData::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponseData> getPopularPicksEneity() {
        List<ProductEntity> productEntities = productRepository.findAll();

        return productEntities.stream()
                .filter(ProductEntity::getIsPopularPicks)
                .map(ProductResponseData::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getFirstProductImage(ProductEntity productEntity) {
        return productEntity.getImages().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No images found for product with ID: " + productEntity.getPid()));
    }


    //Deduce stock
    @Override
    public void deduceStock(TransactionProductEntity transactionProductEntity) {
        try {
            ProductEntity productEntity = productRepository.findByPid(transactionProductEntity.getPid()).orElseThrow(
                    () -> new ProductNotFoundException(transactionProductEntity.getPid())
            );

            if (productEntity.getStock() < 0) {
                throw new ProductNotFoundException(transactionProductEntity.getPid());
            }
            if (transactionProductEntity.getQuantity() <= productEntity.getStock()) {
                productEntity.setStock(productEntity.getStock() - transactionProductEntity.getQuantity());
            } else {
                System.out.println(productEntity.getStock() + " and " + transactionProductEntity.getQuantity());
                throw new StockDeductionException(productEntity.getStock(), transactionProductEntity);
            }

            productRepository.save(productEntity);
        } catch (ProductNotFoundException ex) {
            logger.warn("Product not found.{}", ex.getMessage());
            throw ex;
        }
    }

    private Sort buildSort(String sortBy) {
        return switch (sortBy) {
            case "price-ascending" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-descending" -> Sort.by(Sort.Direction.DESC, "price");
            case "alpha-ascending" -> Sort.by(Sort.Direction.ASC, "name");
            case "alpha-descending" -> Sort.by(Sort.Direction.DESC, "name");
            case "date-descending" -> Sort.by(Sort.Direction.DESC, "_id");
            case "date-ascending" -> Sort.by(Sort.Direction.ASC, "_id");
            default -> Sort.unsorted();
        };
    }

    @Override
    public PriceRangeDto getPriceRange() {
        ProductEntity minProduct = productRepository.findTopByOrderByPriceAsc();
        BigDecimal minPrice = minProduct.getPrice();
        ProductEntity maxProduct = productRepository.findTopByOrderByPriceDesc();
        BigDecimal maxPrice = maxProduct.getPrice();

        return new PriceRangeDto(minPrice, maxPrice);
    }
}
