package com.fsse2406.project.service;

import com.fsse2406.project.data.product.domainObject.ProductResponseData;
import com.fsse2406.project.data.product.dto.PriceRangeDto;
import com.fsse2406.project.data.product.entity.ProductEntity;
import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;

import java.util.List;

public interface ProductService {
    //Get data/entity
//    List<ProductResponseData> getAllProduct();

    //Get data/entity
    //    public List<ProductResponseData> getAllProduct() {
////        List<ProductResponseData> getAllProductList = new ArrayList<>();
////        for (ProductEntity productEntity : productRepository.findAll()) {
////            getAllProductList.add(new ProductResponseData(productEntity));
////        }
////        return getAllProductList;
////    }
//        return productRepository.findAll()
//                .stream()
//                .sorted(Comparator.comparingInt(p -> Integer.parseInt(p.getPid()))) // Convert pid to int for correct order
//                .map(ProductResponseData::new)
//                .toList();
//    }

    //Get data/entity
    List<ProductResponseData> getAllProducts(List<String> categories,
                                             Double minPrice,
                                             Double maxPrice,
                                             String sortBy);

    ProductResponseData getProductDataById(String pid);

    List<ProductEntity> getAllProductEntity();

    ProductEntity getProductEntityByPid(String pid);

    List<ProductResponseData> getAllProductByName(String name);

    List<ProductResponseData> getPopularPicksEneity();

    String getFirstProductImage(ProductEntity productEntity);

    void deduceStock(TransactionProductEntity transactionProductEntity);

    PriceRangeDto getPriceRange();
}
