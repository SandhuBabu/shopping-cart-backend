package com.shoppingcart.controller;


import com.shoppingcart.dto.CartEditResponse;
import com.shoppingcart.exception.UserNotFoundException;
import com.shoppingcart.service.CartService;
import com.shoppingcart.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping("/add/{productId}")
    public ResponseEntity<CartEditResponse> addToCart(
            @PathVariable Long productId, Principal principal
    ) throws Exception {
        String userEmail = principal.getName();

        var res = cartService.addToCart(productId, userEmail);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CartEditResponse> removeFromCart(
            @PathVariable Long productId,
            Principal principal
    ) throws Exception {
        String userEmail = principal.getName();
        var res = cartService.removeFromCart(productId, userEmail);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/find/{productId}")
    public ResponseEntity<Boolean> checkProductExistsInCart(
            @PathVariable Long productId,
            Principal principal
    ) throws UserNotFoundException {
        var userEmail = principal.getName();
        var res = cartService.isProductExistsInCart(productId, userEmail);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartCount(
            Principal principal
    ) throws UserNotFoundException {
        var userEmail = principal.getName();
        var res = cartService.getCartCount(userEmail);
        return ResponseEntity.ok(res);
    }
}

