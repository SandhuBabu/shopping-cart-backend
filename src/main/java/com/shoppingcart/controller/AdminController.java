package com.shoppingcart.controller;

import com.shoppingcart.entity.Product;
import com.shoppingcart.exception.ProductException;
import com.shoppingcart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;

    @PostMapping("/addProduct")
    public ResponseEntity<Product> addProduct(
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam String gender,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam Long stockAvailable,
            @RequestParam MultipartFile image
    ) throws ProductException {

        var product = Product.builder()
                .title(title)
                .category(category)
                .gender(gender)
                .description(description)
                .price(price)
                .stockAvailable(stockAvailable)
                .build();

        var saved = productService.createProduct(product, image);


        if(saved != null)
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        throw new ProductException("Can't create product", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/editProduct/{id}")
    public ResponseEntity<Product> editProduct(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam String gender,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam Long stockAvailable,
            @RequestParam(required = false) MultipartFile image
    ) throws ProductException {

        var product = Product.builder()
                .id(id)
                .title(title)
                .category(category)
                .gender(gender)
                .description(description)
                .price(price)
                .stockAvailable(stockAvailable)
                .build();

        var saved = productService.updateProduct(product, image);


        if(saved != null)
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        throw new ProductException("Can't create product", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
