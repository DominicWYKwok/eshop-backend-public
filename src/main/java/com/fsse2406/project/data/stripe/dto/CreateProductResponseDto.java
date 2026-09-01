package com.fsse2406.project.data.stripe.dto;

import com.fsse2406.project.data.stripe.domainObject.CreateProductResponseData;
import lombok.Data;

import java.util.List;

@Data
public class CreateProductResponseDto {
    private String id;
    private String name;
    private String description;
    private List<String> images;

    public CreateProductResponseDto(CreateProductResponseData createProductResponseData) {
        this.id = createProductResponseData.getId();
        this.name = createProductResponseData.getName();
        this.description = createProductResponseData.getDescription();
        this.images = createProductResponseData.getImages();
    }
}
