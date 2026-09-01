package com.fsse2406.project.data.stripe.dto;

import com.fsse2406.project.data.stripe.domainObject.CreatePriceResponseData;
import lombok.Data;

@Data
public class CreatePriceSuccessResponseDto {
    private String name;
    private String stripeProductId;
    private String currency;
    private Long price;
    private String priceId;
    private String paymentType;

    public CreatePriceSuccessResponseDto(CreatePriceResponseData createPriceResponseData) {
        this.name = createPriceResponseData.getName();
        this.stripeProductId = createPriceResponseData.getStripeProductId();
        this.currency = createPriceResponseData.getCurrency();
        this.price = createPriceResponseData.getPrice();
        this.priceId = createPriceResponseData.getPriceId();
        this.paymentType = createPriceResponseData.getPaymentType();
    }
}
