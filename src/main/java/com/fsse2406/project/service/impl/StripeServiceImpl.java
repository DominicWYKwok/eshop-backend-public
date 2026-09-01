package com.fsse2406.project.service.impl;

import com.fsse2406.project.config.EnvConfig;
import com.fsse2406.project.data.product.entity.ProductEntity;
import com.fsse2406.project.data.stripe.domainObject.CreatePriceResponseData;
import com.fsse2406.project.data.stripe.domainObject.CreateProductResponseData;
import com.fsse2406.project.data.stripe.entity.StripeEntity;
import com.fsse2406.project.data.transactionProduct.entity.TransactionProductEntity;
import com.fsse2406.project.repository.ProductRepository;
import com.fsse2406.project.repository.StripeRepository;
import com.fsse2406.project.service.ProductService;
import com.fsse2406.project.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StripeServiceImpl implements StripeService {

    private final StripeRepository stripeRepository;
    private final ProductService productService;
    private final String domain;
    private final ProductRepository productRepository;

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${spring.profiles.active:dev}") // Default to "dev" if the property is not set
    private String activeProfile;

    public StripeServiceImpl(ProductService productService, StripeRepository stripeRepository, ProductRepository productRepository) {
        this.productService = productService;
        this.stripeRepository = stripeRepository;
        this.domain = "prod".equals(activeProfile)
                ? EnvConfig.PROD_BASE_URL
                : EnvConfig.DEV_BASE_URL;
        this.productRepository = productRepository;
    }

    @Override
    public CreateProductResponseData createProduct(String pid) throws StripeException {

        Stripe.apiKey = secretKey;
        ProductEntity productEntity = productService.getProductEntityByPid(pid);
        String firstImageUrl = productService.getFirstProductImage(productEntity);
        StripeEntity stripeEntity = findOrCreateStripeEntity(productEntity);

        ProductCreateParams params = ProductCreateParams.builder()
                .setName(productEntity.getName())
                .setDescription(productEntity.getSummary())
                .addImage(firstImageUrl)
                .build();
        Product product = Product.create(params);
        setStripeProductId(product.getId(), stripeEntity, productEntity);
        return new CreateProductResponseData(product.getId(), product.getName(), product.getDescription(), product.getImages());
    }

    @Override
    public void createAllProduct() throws StripeException {
        Stripe.apiKey = secretKey;
        List<ProductEntity> productEntities = productService.getAllProductEntity();
        for (ProductEntity productEntity : productEntities) {
            StripeEntity stripeEntity = findOrCreateStripeEntity(productEntity);
            String firstImageUrl = productService.getFirstProductImage(productEntity);
            if (stripeEntity.getStripeProductId() == null) {
                ProductCreateParams params = ProductCreateParams.builder()
                        .setName(productEntity.getName())
                        .setDescription(productEntity.getSummary())
                        .addImage(firstImageUrl)
                        .build();
                Product product = Product.create(params);
                setStripeProductId(product.getId(), stripeEntity, productEntity);
                System.out.println("Created product: " + productEntity.getName());
            } else {
                System.out.println("Product already exists: " + productEntity.getName());
            }
        }
    }

    @Override
    public CreatePriceResponseData createPrice(String pid) throws StripeException {
        Stripe.apiKey = secretKey;
        ProductEntity productEntity = productService.getProductEntityByPid(pid);
        StripeEntity stripeEntity = findOrCreateStripeEntity(productEntity);

        //convert Bigdecimal to Long
//        Long unitAmount = productEntity.getPrice()
//                .multiply(BigDecimal.valueOf(100)) // Convert to cents
//                .longValueExact(); // Ensure no loss of precision
//        unitAmount = Long.valueOf(unitAmount);
        Long unitAmount = productEntity.getPrice()
                .multiply(BigDecimal.valueOf(100)) // Convert to cents
                .longValueExact();

        PriceCreateParams params = PriceCreateParams.builder()
                .setCurrency("hkd")
                .setUnitAmount(unitAmount) // Price per unit in cents ($10.00)
                .setProduct(stripeEntity.getStripeProductId())
                .build();
        Price price = Price.create(params);
        setStripePriceId(price.getId(), stripeEntity, productEntity);
        return new CreatePriceResponseData(
                productEntity.getName(),
                price.getProduct(),
                price.getCurrency(),
                price.getUnitAmount(),
                price.getId(),
                price.getType()
        );
    }

    @Override
    public void createAllPrices() throws StripeException {
        Stripe.apiKey = secretKey;
        List<ProductEntity> productEntities = productService.getAllProductEntity();
        for (ProductEntity productEntity : productEntities) {
            StripeEntity stripeEntity = findOrCreateStripeEntity(productEntity);
            if (stripeEntity.getStripePriceId() == null) {

                //convert Bigdecimal to Long
//        Long unitAmount = productEntity.getPrice()
//                .multiply(BigDecimal.valueOf(100)) // Convert to cents
//                .longValueExact(); // Ensure no loss of precision
//        unitAmount = Long.valueOf(unitAmount);
                Long unitAmount = productEntity.getPrice()
                        .multiply(BigDecimal.valueOf(100)) // Convert to cents
                        .longValueExact();

                PriceCreateParams params = PriceCreateParams.builder()
                        .setCurrency("hkd")
                        .setUnitAmount(unitAmount) // Price per unit in cents ($10.00)
                        .setProduct(stripeEntity.getStripeProductId())
                        .build();
                Price price = Price.create(params);
                setStripePriceId(price.getId(), stripeEntity, productEntity);
                System.out.println("Created price for product: " + productEntity.getName());
            }else {
                System.out.println("Price already exists for product: " + productEntity.getName());
            }
        }
    }

    @Override
    public Map<String, String> createStripeSession
            (List<TransactionProductEntity> transactionProductEntityList, String tid) throws StripeException {
        Stripe.apiKey = secretKey;

        SessionCreateParams.Builder sessionBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(domain + "/payment-success/" + tid)
                .setCancelUrl(domain + "/error");

        for (TransactionProductEntity item : transactionProductEntityList) {
            sessionBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity((long) item.getQuantity())
                            .setPrice(item.getStripeDetails().getStripePriceId())
                            .build()
            );
        }
        SessionCreateParams params = sessionBuilder.build();
        Session session = Session.create(params);
        Map<String, String> response = new HashMap<>();
        response.put("url", session.getUrl());
        response.put("sessionId", session.getId());
        return response;
    }

    @Override
    public String getPaymentStatusBySessionId(String sessionId) throws StripeException {
        Stripe.apiKey = secretKey;
        Session session =
                Session.retrieve(sessionId);
        return session.getStatus();
    }

    public StripeEntity findOrCreateStripeEntity(ProductEntity productEntity) {
        // Check null
        StripeEntity stripeEntity = productEntity.getStripeDetails();
        // If stripeEntity is null, create a new StripeEntity
        if (stripeEntity == null) {
            stripeEntity = new StripeEntity();
            productEntity.setStripeDetails(stripeEntity);
            System.out.println("New stripe entity was created:" + productEntity.getStripeDetails());
            productRepository.save(productEntity);
        }
        return stripeEntity;
    }

    public void setStripeProductId(String stripeProductId, StripeEntity stripeEntity, ProductEntity productEntity) {
        System.out.println("Current stripeProductId in StripeEntity: " + productEntity.getStripeDetails().getStripeProductId());
        System.out.println("New stripeProductId being set: " + stripeProductId);
        stripeEntity.setStripeProductId(stripeProductId);
        productEntity.setStripeDetails(stripeEntity);
        productRepository.save(productEntity);
    }

    public void setStripePriceId(String stripePriceId, StripeEntity stripeEntity, ProductEntity productEntity) {
        System.out.println("Current stripePriceId in StripeEntity: " + stripeEntity.getStripePriceId());
        System.out.println("New stripePriceId being set: " + stripePriceId);
        stripeEntity.setStripePriceId(stripePriceId);
        productEntity.setStripeDetails(stripeEntity);
        productRepository.save(productEntity);
    }

    @Override
    public void getEntity(String pid) {
        ProductEntity productEntity = productService.getProductEntityByPid(pid);
        System.out.println(productEntity.getStripeDetails());
    }
}