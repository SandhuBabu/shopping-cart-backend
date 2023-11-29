package com.shoppingcart.controller;

import com.shoppingcart.dto.CreateOrderRequest;
import com.shoppingcart.dto.OrderCreatedResponse;
import com.shoppingcart.dto.OrderSuccessDto;
import com.shoppingcart.dto.PaymentFailureDto;
import com.shoppingcart.entity.Orders;
import com.shoppingcart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderCreatedResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest, Principal principal) {

        var userEmail = principal.getName();
        var order = orderService.createOrder(createOrderRequest, userEmail);
        if (order == null)
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PostMapping("/payment/success")
    public ResponseEntity<String> orderSuccess(@RequestBody OrderSuccessDto orderSuccessDto) {
        System.out.println(orderSuccessDto);
        var res = orderService.paymentSuccess(orderSuccessDto);
        if (res == null)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save payment details, if order is not showing contact customer care");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/payment/failure")
    public ResponseEntity<String> paymentFailed(@RequestBody PaymentFailureDto paymentFailureDto) {
        var res = orderService.paymentFailed(paymentFailureDto);
        return ResponseEntity.ok(res);
    }
}
