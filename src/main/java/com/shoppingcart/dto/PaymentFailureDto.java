package com.shoppingcart.dto;

import lombok.Data;

@Data
public class PaymentFailureDto {
    private String orderId;
    private String paymentId;
}
