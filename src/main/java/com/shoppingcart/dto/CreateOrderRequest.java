package com.shoppingcart.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrderRequest {
    private Long addressId;
    private Long productId;
    private Integer price;
    private Integer quantity;
}
