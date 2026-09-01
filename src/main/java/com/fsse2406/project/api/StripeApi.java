package com.fsse2406.project.api;

import com.fsse2406.project.config.EnvConfig;
import com.fsse2406.project.data.stripe.dto.CreatePriceSuccessResponseDto;
import com.fsse2406.project.data.stripe.dto.CreateProductResponseDto;
import com.fsse2406.project.service.StripeService;
import com.stripe.exception.StripeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
@Profile("development")
@RequestMapping("/stripe")
@CrossOrigin({EnvConfig.DEV_BASE_URL, EnvConfig.PROD_BASE_URL})
public class StripeApi {
    private static final Logger logger = LoggerFactory.getLogger(StripeApi.class);
    private final StripeService stripeService;

    public StripeApi(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @GetMapping("/get/{pid}")
    public void getEntity(@PathVariable String pid) {
        stripeService.getEntity(pid);
    }

    @PostMapping("/create/product/{pid}")
    public CreateProductResponseDto createProductById(@PathVariable String pid) {
        try {
            return new CreateProductResponseDto(stripeService.createProduct(pid));
        } catch (StripeException e) {
            logger.error("Failed to create product in Stripe for pid: {}", pid, e);
            throw new RuntimeException("Failed to create product in Stripe");
        }
    }

    @PostMapping("/create/product/all")
    public void createAllProduct() {
        try {
            stripeService.createAllProduct();
        } catch (StripeException e) {
            logger.error("Failed to create all products in Stripe", e);
            throw new RuntimeException("Failed to create all products in Stripe");
        }
    }

    @PostMapping("/create/price/{pid}")
    public CreatePriceSuccessResponseDto createPrice(@PathVariable String pid) {
        try {
            return new CreatePriceSuccessResponseDto(stripeService.createPrice(pid));
        } catch (StripeException e) {
            logger.error("Failed to create price in Stripe for pid: {}", pid, e);
            throw new RuntimeException("Failed to create price in Stripe");
        }
    }

    @PostMapping("/create/price/all")
    public void createAllPrices() {
        try {
            stripeService.createAllPrices();
        } catch (StripeException e) {
            logger.error("Failed to create all product prices in Stripe", e);
            throw new RuntimeException("Failed to create all product prices in Stripe");
        }
    }
}


//    @PostMapping("/create/transaction")
//    public String createP() throws StripeException {
//        String DomainPort = "http://localhost:8080";
//        SessionCreateParams params = SessionCreateParams.builder()
//                .setMode(SessionCreateParams.Mode.PAYMENT)
//                .setSuccessUrl(DomainPort + "/success")
//                .setCancelUrl(DomainPort + "/cancel")
//                .addLineItem(
//                        SessionCreateParams.LineItem.builder()
//                                .setQuantity(1L)
//                                .setPrice("{{PRICE_ID}}") // Replace with actual Price ID
//                                .build()
//                )
//                .build();
//
//        Session session = Session.create(params);
//        return session.getUrl();
//    }
