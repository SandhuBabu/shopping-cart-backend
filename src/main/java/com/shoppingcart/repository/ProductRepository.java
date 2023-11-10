package com.shoppingcart.repository;

import com.shoppingcart.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p ORDER BY p.id DESC LIMIT 5")
    public List<Product> getNewArrivals();

    @Query("SELECT p FROM Product p WHERE p.stockAvailable > 1 AND p.price < 500 ORDER BY RAND() LIMIT 5")
    public List<Product> getRandomBudgetProducts();

    public List<Product> findByPriceLessThan(double price);

    public List<Product> findByGender(String gender);
}
