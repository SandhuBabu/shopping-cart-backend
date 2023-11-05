package com.shoppingcart.controller;


import com.shoppingcart.entity.Product;
import com.shoppingcart.exception.ProductException;
import com.shoppingcart.repository.ProductRepository;
import com.shoppingcart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/open/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) throws ProductException {
        var product = productService.getProductById(id);

        if(product == null)
            throw new ProductException("No product found with id "+id, HttpStatus.NOT_FOUND);

        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getPagedProducts(@RequestParam(required = false, defaultValue = "1") Integer pageNo) {
        System.out.println(pageNo);
        var products = productService.getAllProductsPaginated(pageNo);
        return ResponseEntity.ok(products);
    }
}
