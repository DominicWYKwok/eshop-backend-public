package com.fsse2406.project.api;

import com.fsse2406.project.data.product.domainObject.ProductResponseData;
import com.fsse2406.project.data.product.entity.ProductEntity;
import com.fsse2406.project.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class ProductApiTest {

    @Mock
    private ProductService productService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new ProductApi(productService)).build();
    }

    @Test
    void getAllProductsReturnsProductResponse() throws Exception {
        ProductEntity product = new ProductEntity();
        product.setPid("p-1");
        product.setName("Coffee mug");
        product.setPrice(new BigDecimal("48.00"));
        product.setStock(4);

        when(productService.getAllProducts(null, null, null, ""))
                .thenReturn(List.of(new ProductResponseData(product)));

        mockMvc.perform(get("/public/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].pid").value("p-1"))
                .andExpect(jsonPath("$[0].name").value("Coffee mug"))
                .andExpect(jsonPath("$[0].hasStock").value(true));

        verify(productService).getAllProducts(null, null, null, "");
    }
}
