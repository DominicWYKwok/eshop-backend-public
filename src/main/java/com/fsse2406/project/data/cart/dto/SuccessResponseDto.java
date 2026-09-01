package com.fsse2406.project.data.cart.dto;

import lombok.Data;

@Data
public class SuccessResponseDto {
    private String result;

    public SuccessResponseDto() {
        setResult("SUCCESS");
    }
}

//public class CartItemResponseDto {
//    private boolean addStatus;
//
//    public CartItemResponseDto(CartItemResponseData cartItemResponseData) {
//        this.addStatus = cartItemResponseData.getAddCartStatus();
//    }
//
//    public boolean isAddStatus() {
//        return addStatus;
//    }
//
//    public void setAddStatus(boolean addStatus) {
//        this.addStatus = addStatus;
//    }
//}
