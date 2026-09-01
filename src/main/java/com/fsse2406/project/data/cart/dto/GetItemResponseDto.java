package com.fsse2406.project.data.cart.dto;

import com.fsse2406.project.data.cart.domainObject.CartItemResponseData;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GetItemResponseDto {
    private String pid;
    private String name;
    private List<String> images;
    private BigDecimal price;
    private Integer cartQuantity;
    private Integer stock;

    public GetItemResponseDto(CartItemResponseData cartItemResponseData) {
        this.pid = cartItemResponseData.getProduct().getPid();
        this.name = cartItemResponseData.getProduct().getName();
        this.images = cartItemResponseData.getProduct().getImages();
        this.price = cartItemResponseData.getProduct().getPrice();
        this.cartQuantity = cartItemResponseData.getQuantity();
        this.stock = cartItemResponseData.getProduct().getStock();
    }
}