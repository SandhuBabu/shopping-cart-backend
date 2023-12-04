package com.shoppingcart.controller;

import com.shoppingcart.dto.*;
import com.shoppingcart.exception.UserNotFoundException;
import com.shoppingcart.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @GetMapping
    public ResponseEntity<List<OrderDto>> allOrders(Principal principal){
        var email = principal.getName();
        var res = orderService.getAllUsersOrder(email);
        return ResponseEntity.ok(res);
    }

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

    @PostMapping("{orderId}/addRating/{rating}")
    public ResponseEntity<String> addRating(@PathVariable Long orderId, @PathVariable Integer rating, Principal principal) throws UserNotFoundException {
        if(rating > 0 && rating <= 5) {
            var email = principal.getName();
            var res=orderService.addRatingToOrder(orderId, rating, email);
            if (res == null)
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("order not found or failed to add rating");
            return ResponseEntity.ok(res);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid rating");
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Object> cancel(@PathVariable Long id, Principal principal) {
        var email = principal.getName();
        var res = orderService.cancelOrder(id, email);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/return/{orderId}")
    public ResponseEntity<String> returnOrder(@PathVariable Long orderId, Principal principal) throws UserNotFoundException {
        var email = principal.getName();
        if(orderService.checkAuthorizedUserAction(email, orderId) == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sorry, can't perform action");

        var res = orderService.changeStatus(orderId, "returned");
        return ResponseEntity.ok(res);
    }
}
