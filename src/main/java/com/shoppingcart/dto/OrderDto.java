package com.shoppingcart.dto;

import com.shoppingcart.entity.Address;
import com.shoppingcart.entity.Product;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDto {
    private Long id;
    private String razorpayOrderId;
    private String paymentId;
    private String status;
    private String paymentStatus;
    private String razorpaySignature;
    private Integer quantity;
    private Integer totalAmount;
    private String fullName;
    private String email;
    private Long mobile;
    private Product product;
    private Address address;
}
