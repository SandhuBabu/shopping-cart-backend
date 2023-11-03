package com.shoppingcart.service;

import com.shoppingcart.entity.Product;
import com.shoppingcart.repository.ProductRepository;
import com.shoppingcart.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(Product product, MultipartFile file) {
        var savedProduct = productRepository.save(product);
        FileUtils fileUtils = new FileUtils();
        var imageUrl = FileUtils.saveImage(file, savedProduct.getId());
        if(imageUrl == null) {
            productRepository.delete(savedProduct);
            return null;
        }
        savedProduct.setImageUrl(imageUrl);
        productRepository.save(savedProduct);
        return savedProduct;
    }
}
