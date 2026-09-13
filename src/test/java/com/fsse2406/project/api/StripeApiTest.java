package com.fsse2406.project.api;

import com.fsse2406.project.data.stripe.domainObject.CreateProductResponseData;
import com.fsse2406.project.service.StripeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class StripeApiTest {

    @Mock
    private StripeService stripeService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new StripeApi(stripeService)).build();
    }

    @Test
    void createProductDelegatesToMockedStripeService() throws Exception {
        when(stripeService.createProduct("p-1"))
                .thenReturn(new CreateProductResponseData(
                        "prod_test",
                        "Coffee mug",
                        "A mug",
                        List.of("https://example.com/mug.png")
                ));

        mockMvc.perform(post("/stripe/create/product/p-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("prod_test"))
                .andExpect(jsonPath("$.name").value("Coffee mug"));

        verify(stripeService).createProduct("p-1");
    }
}
