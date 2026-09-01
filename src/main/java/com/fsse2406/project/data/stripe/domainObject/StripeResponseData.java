package com.fsse2406.project.data.stripe.domainObject;

import com.fsse2406.project.data.stripe.entity.StripeEntity;
import lombok.Data;

@Data
public class StripeResponseData {
    private String stripeProductId;
    private String stripePriceId;

    public StripeResponseData(StripeEntity stripeEntity) {
        if (stripeEntity != null) {
            this.stripeProductId = stripeEntity.getStripeProductId();
            this.stripePriceId = stripeEntity.getStripePriceId();
        }
    }
}
