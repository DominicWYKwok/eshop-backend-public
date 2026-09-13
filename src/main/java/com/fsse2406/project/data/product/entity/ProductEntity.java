package com.fsse2406.project.data.product.entity;

import com.fsse2406.project.data.stripe.entity.StripeEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "products")
public class ProductEntity {

    @Id
    private String productObjectId;

    private String pid;

    private String name;

    private List<String> images = new ArrayList<>();

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;

    private Integer stock;

    private String summary;

    private List<String> descrImages = new ArrayList<>();

    private StripeEntity stripeDetails;

    private Boolean isPopularPicks;

    private String category;
}
