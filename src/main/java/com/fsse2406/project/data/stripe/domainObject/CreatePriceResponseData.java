package com.fsse2406.project.data.stripe.domainObject;

import lombok.Data;

@Data
public class CreatePriceResponseData {
    private String name;
    private String stripeProductId;
    private String currency;
    private Long price;
    private String priceId;
    private String paymentType;

    public CreatePriceResponseData(String name, String stripeProductId, String currency, Long price, String priceId, String paymentType) {
        this.name = name;
        this.stripeProductId = stripeProductId;
        this.currency = currency;
        this.price = price;
        this.priceId = priceId;
        this.paymentType = paymentType;
    }
}
