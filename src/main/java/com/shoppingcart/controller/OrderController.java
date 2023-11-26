package com.shoppingcart.controller;

import com.shoppingcart.dto.CreateOrderRequest;
import com.shoppingcart.dto.OrderCreatedResponse;
import com.shoppingcart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderCreatedResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        System.out.println("IN ORDER CTRLR");
        var order = orderService.createOrder(createOrderRequest);
        if(order == null)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

}
