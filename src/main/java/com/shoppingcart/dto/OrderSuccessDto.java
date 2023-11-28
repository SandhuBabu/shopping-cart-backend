package com.shoppingcart.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderSuccessDto {
    private String razorpayOderId;
    private Long addressId;
    private String paymentId;
    private String razorpaySignature;
    private List<ProductData> products;

    @Data
    static class ProductData {
        Integer id;
        Integer price;
        Integer quantity;
    }
}