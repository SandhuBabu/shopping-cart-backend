package com.shoppingcart.repository;

import com.shoppingcart.entity.Orders;
import com.shoppingcart.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByRazorpayOrderId(String id);

    Page<Orders> findByStatusNot(String status, Pageable pageable);

    Integer countByStatus(String status);
}
