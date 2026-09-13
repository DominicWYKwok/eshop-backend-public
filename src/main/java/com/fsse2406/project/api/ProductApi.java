package com.fsse2406.project.api;

import com.fsse2406.project.config.EnvConfig;
import com.fsse2406.project.data.product.domainObject.ProductResponseData;
import com.fsse2406.project.data.product.dto.PriceRangeDto;
import com.fsse2406.project.data.product.dto.ProductGetByIdResponseDto;
import com.fsse2406.project.data.product.dto.ProductResponseDto;
import com.fsse2406.project.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/product")
@CrossOrigin({EnvConfig.DEV_BASE_URL, EnvConfig.PROD_BASE_URL})
public class ProductApi {
    private final ProductService productService;

    public ProductApi(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponseDto> getAllProducts(
            @RequestParam(name = "categories", required = false) List<String> categories,
            @RequestParam(name = "min_price", required = false) Double minPrice,
            @RequestParam(name = "max_price", required = false) Double maxPrice,
            @RequestParam(name = "sort_by", required = false, defaultValue = "") String sortBy
    ) {
        List<ProductResponseData> dataList = productService.getAllProducts(categories, minPrice, maxPrice, sortBy);

        return dataList.stream()
                .map(ProductResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductGetByIdResponseDto getProductById(@PathVariable String id) {
        return new ProductGetByIdResponseDto(productService.getProductDataById(id));
    }

    @GetMapping("/search")
    public List<ProductResponseDto> getProductByName(@RequestParam String q) {
        List<ProductResponseData> getAllProductDataList = productService.getAllProductByName(q);

        List<ProductResponseDto> getAllProductDtoList = new ArrayList<>();
        for (ProductResponseData productResponseData : getAllProductDataList) {
            ProductResponseDto productResponseDto = new ProductResponseDto(productResponseData);
            getAllProductDtoList.add(productResponseDto);
        }
        return getAllProductDtoList;
    }

    @GetMapping("/ispopular")
    public List<ProductResponseDto> getPopularPicks() {
        List<ProductResponseData> getAllProductDataList = productService.getPopularPicksEneity();

        List<ProductResponseDto> getAllProductDtoList = new ArrayList<>();
        for (ProductResponseData productResponseData : getAllProductDataList) {
            ProductResponseDto productResponseDto = new ProductResponseDto(productResponseData);
            getAllProductDtoList.add(productResponseDto);
        }
        return getAllProductDtoList;
    }

    @GetMapping("/pricerange")
    public PriceRangeDto getPriceRange() {
        return productService.getPriceRange();
    }
}