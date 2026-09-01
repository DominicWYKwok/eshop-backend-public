package com.fsse2406.project.data.stripe.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "stripe")
public class StripeEntity {
    private String stripeProductId;
    private String stripePriceId;
}
