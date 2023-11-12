package com.shoppingcart.repository;

import com.shoppingcart.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p ORDER BY p.id DESC LIMIT 5")
    public List<Product> getNewArrivals();

    @Query("SELECT p FROM Product p WHERE p.stockAvailable > 1 AND p.price < 500 ORDER BY RAND() LIMIT 5")
    public List<Product> getRandomBudgetProducts();

    public List<Product> findByPriceLessThan(double price);

    public List<Product> findByGender(String gender);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    default Page<Product> findByFilters(String title, String gender, String category, Double price, Pageable pageable) {
        return findAll((root, query, criteriaBuilder) -> {
            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            if(StringUtils.hasText(title)) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }

            if(StringUtils.hasText(gender)) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), gender));
            }
            if(StringUtils.hasText(category)) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }
            if(price != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), price));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
