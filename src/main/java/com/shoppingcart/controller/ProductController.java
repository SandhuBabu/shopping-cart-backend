package com.shoppingcart.controller;


import com.shoppingcart.dto.PaginationResponse;
import com.shoppingcart.entity.Product;
import com.shoppingcart.exception.ProductException;
import com.shoppingcart.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PaginationResponse<Product>> getPagedProducts(
            @RequestParam(required = false, defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(required = false, defaultValue = "false") Boolean sort
    ) {
        if(pageNo < 1)
            pageNo = 1;
        var products = productService.getAllProductsPaginated(pageNo, pageSize, sort);

        return ResponseEntity.ok(products);
    }
}
