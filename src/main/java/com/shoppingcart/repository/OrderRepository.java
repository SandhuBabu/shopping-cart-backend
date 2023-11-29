package com.shoppingcart.repository;

import com.shoppingcart.entity.Orders;
import com.shoppingcart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByRazorpayOrderId(String id);


    @Query("SELECT o from Orders o where o.user=:user ORDER BY id DESC LIMIT 1")
    Orders findLastOrderOfUser(User user);


    List<Orders> findByStatusNot(String status);
}
