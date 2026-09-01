package com.fsse2406.project.data.stripe.dto;

import com.fsse2406.project.data.stripe.domainObject.StripeResponseData;
import lombok.Data;

@Data
public class StripeResponseDto {
    private String stripeProductId;
    private String stripePriceId;

    public StripeResponseDto(StripeResponseData stripeResponseData) {
        if (stripeResponseData != null) {
            this.stripeProductId = stripeResponseData.getStripeProductId();
            this.stripePriceId = stripeResponseData.getStripePriceId();
        }
    }
}

