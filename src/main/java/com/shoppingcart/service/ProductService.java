package com.shoppingcart.service;

import com.shoppingcart.dto.PaginationResponse;
import com.shoppingcart.entity.Product;
import com.shoppingcart.exception.ProductException;
import com.shoppingcart.repository.ProductRepository;
import com.shoppingcart.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
        } catch (Exception e) {
            throw new ProductException("Can't update product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Product getProductById(Long id) {
        var product = productRepository.findById(id);
        return product.orElse(null);
    }

    public PaginationResponse<Product> getAllProductsPaginated(int pageNo, int pageSize) {

        Pageable page = PageRequest.of(pageNo-1, pageSize);
        var productsResult = productRepository.findAll(page);
        PaginationResponse<Product> response = new PaginationResponse<Product>();
        response.setContent(productsResult.getContent());
        response.setTotalPages(productsResult.getTotalPages());
        response.setFirst(productsResult.isFirst());
        response.setLast(productsResult.isLast());
        response.setPageNo(pageNo);
        response.setEmpty(productsResult.isEmpty());

        return response;
    }
}
