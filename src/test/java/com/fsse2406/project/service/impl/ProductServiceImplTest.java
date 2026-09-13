package com.fsse2406.project.service.impl;

import com.fsse2406.project.data.product.domainObject.ProductResponseData;
import com.fsse2406.project.data.product.dto.PriceRangeDto;
import com.fsse2406.project.data.product.entity.ProductEntity;
import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;
import com.fsse2406.project.exception.product.ProductNotFoundException;
import com.fsse2406.project.exception.product.StockDeductionException;
import com.fsse2406.project.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    private ProductServiceImpl service() {
        return new ProductServiceImpl(productRepository, mongoTemplate);
    }

    @Test
    void getAllProductByNameMapsRepositoryResultsToResponseData() {
        ProductEntity product = new ProductEntity();
        product.setPid("p-1");
        product.setName("Coffee mug");
        product.setStock(4);

        when(productRepository.findByNameContaining("mug"))
                .thenReturn(List.of(product));

        List<ProductResponseData> result = service().getAllProductByName("mug");

        assertEquals(1, result.size());
        assertEquals("p-1", result.get(0).getPid());
        assertEquals("Coffee mug", result.get(0).getName());
        verify(productRepository).findByNameContaining("mug");
    }

    @Test
    void getProductDataByIdReturnsMappedProductWhenFound() {
        ProductEntity product = product("p-1", "Coffee mug", 4);
        when(productRepository.findByPid("p-1")).thenReturn(Optional.of(product));

        ProductResponseData result = service().getProductDataById("p-1");

        assertEquals("p-1", result.getPid());
        assertEquals("Coffee mug", result.getName());
        assertEquals(4, result.getStock());
    }

    @Test
    void getProductDataByIdThrowsWhenProductDoesNotExist() {
        when(productRepository.findByPid("missing")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> service().getProductDataById("missing"));
    }

    @Test
    void getProductEntityByPidReturnsEntityWhenFound() {
        ProductEntity product = product("p-1", "Coffee mug", 4);
        when(productRepository.findByPid("p-1")).thenReturn(Optional.of(product));

        assertEquals(product, service().getProductEntityByPid("p-1"));
    }

    @Test
    void getProductEntityByPidThrowsWhenProductDoesNotExist() {
        when(productRepository.findByPid("missing")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> service().getProductEntityByPid("missing"));
    }

    @Test
    void getAllProductsSortsByPidNumericallyByDefault() {
        ProductEntity ten = product("10", "Ten", 1);
        ProductEntity two = product("2", "Two", 1);
        when(mongoTemplate.find(any(Query.class), eq(ProductEntity.class)))
                .thenReturn(List.of(ten, two));

        List<ProductResponseData> result = service().getAllProducts(null, null, null, "pid");

        assertEquals(List.of("2", "10"),
                result.stream().map(ProductResponseData::getPid).toList());
    }

    @Test
    void getAllProductsAddsCategoryAndPriceFiltersToQuery() {
        when(mongoTemplate.find(any(Query.class), eq(ProductEntity.class)))
                .thenReturn(List.of());

        service().getAllProducts(List.of("drink"), 10.0, 50.0, "pid");

        ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate).find(captor.capture(), eq(ProductEntity.class));
        Query query = captor.getValue();

        org.bson.Document category = query.getQueryObject().get("category", org.bson.Document.class);
        assertEquals(List.of("drink"), category.getList("$in", String.class));
        assertEquals(10.0, query.getQueryObject().get("price", org.bson.Document.class).get("$gte"));
        assertEquals(50.0, query.getQueryObject().get("price", org.bson.Document.class).get("$lte"));
    }

    @Test
    void getAllProductsAddsRequestedSortToQuery() {
        when(mongoTemplate.find(any(Query.class), eq(ProductEntity.class)))
                .thenReturn(List.of());

        service().getAllProducts(null, null, null, "price-descending");

        ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate).find(captor.capture(), eq(ProductEntity.class));

        assertEquals(-1, captor.getValue().getSortObject().get("price"));
    }

    @Test
    void getPopularPicksReturnsOnlyPopularProducts() {
        ProductEntity popular = product("p-1", "Popular", 1);
        popular.setIsPopularPicks(true);
        ProductEntity regular = product("p-2", "Regular", 1);
        regular.setIsPopularPicks(false);
        when(productRepository.findAll()).thenReturn(List.of(popular, regular));

        List<ProductResponseData> result = service().getPopularPicksEneity();

        assertEquals(List.of("p-1"), result.stream().map(ProductResponseData::getPid).toList());
    }

    @Test
    void getFirstProductImageReturnsFirstImage() {
        ProductEntity product = product("p-1", "Coffee mug", 1);
        product.setImages(List.of("first.jpg", "second.jpg"));

        assertEquals("first.jpg", service().getFirstProductImage(product));
    }

    @Test
    void getFirstProductImageThrowsWhenThereAreNoImages() {
        ProductEntity product = product("p-1", "Coffee mug", 1);

        assertThrows(IllegalArgumentException.class,
                () -> service().getFirstProductImage(product));
    }

    @Test
    void deduceStockReducesStockWhenQuantityIsAvailable() {
        ProductEntity product = product("p-1", "Coffee mug", 10);
        TransactionProductEntity transactionProduct = transactionProduct("p-1", 3);
        when(productRepository.findByPid("p-1")).thenReturn(Optional.of(product));

        service().deduceStock(transactionProduct);

        assertEquals(7, product.getStock());
        verify(productRepository).save(product);
    }

    @Test
    void deduceStockThrowsAndDoesNotSaveWhenStockIsInsufficient() {
        ProductEntity product = product("p-1", "Coffee mug", 2);
        TransactionProductEntity transactionProduct = transactionProduct("p-1", 3);
        when(productRepository.findByPid("p-1")).thenReturn(Optional.of(product));

        assertThrows(StockDeductionException.class,
                () -> service().deduceStock(transactionProduct));

        assertEquals(2, product.getStock());
        verify(productRepository, never()).save(any(ProductEntity.class));
    }

    @Test
    void getPriceRangeReturnsLowestAndHighestPrices() {
        ProductEntity cheapest = product("p-1", "Cheap", 1);
        cheapest.setPrice(new BigDecimal("10.00"));
        ProductEntity expensive = product("p-2", "Expensive", 1);
        expensive.setPrice(new BigDecimal("99.00"));
        when(productRepository.findTopByOrderByPriceAsc()).thenReturn(cheapest);
        when(productRepository.findTopByOrderByPriceDesc()).thenReturn(expensive);

        PriceRangeDto result = service().getPriceRange();

        assertEquals(new BigDecimal("10.00"), result.getMinPrice());
        assertEquals(new BigDecimal("99.00"), result.getMaxPrice());
    }

    private ProductEntity product(String pid, String name, int stock) {
        ProductEntity product = new ProductEntity();
        product.setPid(pid);
        product.setName(name);
        product.setStock(stock);
        product.setPrice(BigDecimal.ONE);
        product.setIsPopularPicks(false);
        return product;
    }

    private TransactionProductEntity transactionProduct(String pid, int quantity) {
        TransactionProductEntity transactionProduct = new TransactionProductEntity();
        transactionProduct.setPid(pid);
        transactionProduct.setQuantity(quantity);
        return transactionProduct;
    }
}
