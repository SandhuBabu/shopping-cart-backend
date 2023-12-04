package com.shoppingcart.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class OrderSuccessDto {
    private String razorpayOderId;
    private String paymentId;
    private String razorpaySignature;
}