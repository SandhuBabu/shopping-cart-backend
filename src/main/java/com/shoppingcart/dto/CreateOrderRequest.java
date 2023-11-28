package com.shoppingcart.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateOrderRequest {
    private Long addressId;
    private List<ProductList> products;
    private Integer totalAmount;

    @Data
    public static class ProductList{
        private Long id;
        private Integer quantity;
    }
}
