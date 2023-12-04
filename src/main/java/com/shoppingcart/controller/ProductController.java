package com.shoppingcart.controller;


import com.shoppingcart.dto.PaginationResponse;
import com.shoppingcart.entity.Product;
import com.shoppingcart.exception.ProductException;
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
    public ResponseEntity<PaginationResponse<Product>> getPagedProducts(
            @RequestParam(required = false, defaultValue = "1") Integer pageNo,
            @RequestParam(required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(required = false, defaultValue = "false") Boolean sort
    ) {
        if(pageNo < 1)
            pageNo = 1;
        if(pageSize < 1)
            pageSize = 5;
        var products = productService.getAllProductsPaginated(pageNo, pageSize, sort);

        return ResponseEntity.ok(products);
    }

    @GetMapping("/newArrivals")
    public ResponseEntity<List<Product>> getNewArrivals() {
        var products = productService.getFourNewArrivals();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/budgetProducts")
    public ResponseEntity<List<Product>> getBudgetProducts() {
        var products = productService.getBudgetRandomProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/priceLimit/{price}")
    public ResponseEntity<List<Product>> getProductsByPriceLimit(@PathVariable Integer price) {
        var product = productService.findByPrice(price);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/gender")
    public ResponseEntity<List<Product>> findProductsByGender(
            @RequestParam(defaultValue = "unisex") String gender
    ) {
        var products = productService.findProductsByGender(gender);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/searchWithFilters")
    public ResponseEntity<PaginationResponse<Product>> searchWithFilters(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "pageNo", required = false) Integer pageNo
    ) {
      try {
          if(pageNo == null || pageNo < 1)
              pageNo=1;
          var products = productService.searchWithFilters(title, gender, category, price, pageNo-1, 2);
          return ResponseEntity.ok(products);
      } catch (Exception e) {
          return ResponseEntity.ok(new PaginationResponse<Product>());
      }
    }

    @GetMapping("/search/{term}")
    public ResponseEntity<PaginationResponse<Product>> searchWithTerm(
            @PathVariable String term,
            @RequestParam(defaultValue = "1") int pageNo
    ) {
        if(pageNo<1) pageNo=1;
        var products = productService.searchResults(term, pageNo-1, 3);
        return ResponseEntity.ok(products);
    }
}
