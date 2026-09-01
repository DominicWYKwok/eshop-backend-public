package com.fsse2406.project.data.stripe.domainObject;

import lombok.Data;

import java.util.List;

@Data
public class CreateProductResponseData {
    private String id;
    private String name;
    private String description;
    private List<String> images;

    public CreateProductResponseData(String id, String name, String description, List<String> images) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.images = images;
    }
}
