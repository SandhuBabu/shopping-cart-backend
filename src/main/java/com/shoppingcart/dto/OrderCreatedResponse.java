package com.shoppingcart.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderCreatedResponse {
    private String orderId;
    private String currency;
    private Integer amount;
}
