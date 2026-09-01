package com.fsse2406.project.data.transaction.dto;

import lombok.Data;

@Data
public class PaySuccessfullyResponseDto {
    private String stripeSessionUrl;

  public PaySuccessfullyResponseDto(String stripeSessionUrl) {
      this.stripeSessionUrl = stripeSessionUrl;
  }
}