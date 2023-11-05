package com.shoppingcart.service;

import com.shoppingcart.entity.Product;
import com.shoppingcart.exception.ProductException;
import com.shoppingcart.repository.ProductRepository;
import com.shoppingcart.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final FileUtils fileUtils;

    public Product createProduct(Product product, MultipartFile file) {
        var savedProduct = productRepository.save(product);
        var imageUrl = fileUtils.saveImage(file, savedProduct.getId());
        if (imageUrl == null) {
            productRepository.delete(savedProduct);
            return null;
        }
        savedProduct.setImageUrl(imageUrl);
        productRepository.save(savedProduct);
        return savedProduct;
    }

    public Product updateProduct(Product product, MultipartFile file) throws ProductException {
        try {
            var savedImageUrl = productRepository.findById(product.getId()).get().getImageUrl();

            if (file != null) {
                var isSaved = fileUtils.deleteImage(savedImageUrl.substring(38));
                if (!isSaved) {
                    throw new ProductException("Can't update image", HttpStatus.INTERNAL_SERVER_ERROR);
                }
                savedImageUrl = fileUtils.saveImage(file, product.getId());
            }
            product.setImageUrl(savedImageUrl);
            return productRepository.save(product);
        } catch (DataIntegrityViolationException e) {
            throw new ProductException("Can't update, some issue with given data", HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            throw new ProductException("Can't update product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Product getProductById(Long id) {
        var product = productRepository.findById(id);
        return product.orElse(null);
    }

    public List<Product> getAllProductsPaginated(int pageNo) {
        return productRepository.findAll();
    }
}
