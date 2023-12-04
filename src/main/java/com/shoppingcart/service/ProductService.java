package com.shoppingcart.service;

import com.shoppingcart.dto.PaginationResponse;
import com.shoppingcart.entity.Product;
import com.shoppingcart.exception.ProductException;
import com.shoppingcart.repository.ProductRepository;
import com.shoppingcart.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        } catch (Exception e) {
            throw new ProductException("Can't update product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Product getProductById(Long id) {
        var product = productRepository.findById(id);
        return product.orElse(null);
    }

    public String deleteProductById(Long id) {
        System.out.println("Product deleting");
        var product = productRepository.findById(id);
        System.out.println(product);
        if (product.isEmpty()) {
            return null;
        }
        System.out.println("Product deleted successfully");
        productRepository.delete(product.get());
        fileUtils.deleteImage(product.get().getImageUrl().substring(38));
        return "Product deleted successfully";
    }

    public PaginationResponse<Product> getAllProductsPaginated(int pageNo, int pageSize, boolean sort) {

        Pageable page = null;
        if (sort) {
            page = PageRequest.of(pageNo - 1, pageSize, Sort.by("id").descending());
        } else {
            page = PageRequest.of(pageNo - 1, pageSize);
        }

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

    public List<Product> getFourNewArrivals() {
        return productRepository.getNewArrivals();
    }

    public List<Product> getBudgetRandomProducts() {
        return productRepository.getRandomBudgetProducts();
    }

    public List<Product> findByPrice(Integer price) {
        return productRepository.findByPriceLessThan(price);
    }

    public List<Product> findProductsByGender(String gender) {
        return productRepository.findByGender(gender);
    }

    public PaginationResponse<Product> searchWithFilters(String title, String gender, String category, Double price, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        var products = productRepository.findByFilters(title, gender, category, price, pageable);
        return getPaginatedResponse(products);
    }

    public PaginationResponse<Product> searchResults(String term, int pageNo, int pageSize) {
        Pageable pageable=PageRequest.of(pageNo, pageSize);
        var products = productRepository.getSearchResults(term, pageable);
        return getPaginatedResponse(products);
    }

    public List<Product> getOutOfStockProducts() {
        return productRepository.findByStockAvailableLessThan(1);
    }

    public  Product updateStock (Long id, Long nos) {
        var product = productRepository.findById(id).get();
        product.setStockAvailable(nos);
        return productRepository.save(product);
    }
    private PaginationResponse<Product> getPaginatedResponse(Page<Product> products) {
        PaginationResponse<Product> response = new PaginationResponse<Product>();
        response.setContent(products.getContent());
        response.setTotalPages(products.getTotalPages());
        response.setFirst(products.isFirst());
        response.setLast(products.isLast());
        response.setPageNo(products.getNumber()+1);
        response.setEmpty(products.isEmpty());

        return response;
    }
}
