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
    private final FileUtils fileUtils;

    public Product createProduct(Product product, MultipartFile file) {
        var savedProduct = productRepository.save(product);
        var imageUrl = fileUtils.saveImage(file, savedProduct.getId());
        if(imageUrl == null) {
            productRepository.delete(savedProduct);
            return null;
        }
        savedProduct.setImageUrl(imageUrl);
        productRepository.save(savedProduct);
        return savedProduct;
    }

    public Product updateProduct(Product product, MultipartFile file) {
        var savedProduct = productRepository.findById(product.getId()).get();
        String savedImageUrl = product.getImageUrl();
        if(file!=null) {
            String imageUrl = savedProduct.getImageUrl();
            fileUtils.deleteImage(imageUrl.substring(38));
        }

        var newProduct = productRepository.save(product);
        if(file != null) {
            String imageUrl = fileUtils.saveImage(file, newProduct.getId());
            if(imageUrl == null) {
                productRepository.delete(newProduct);
                return null;
            }
            savedImageUrl = imageUrl;
            newProduct.setImageUrl(savedImageUrl);
        }
        productRepository.save(newProduct);
        return newProduct;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).get();
    }
}
