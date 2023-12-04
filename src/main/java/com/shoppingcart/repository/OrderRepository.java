package com.shoppingcart.repository;

import com.shoppingcart.entity.Orders;
import com.shoppingcart.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByUserAndStatusNot(User user, String status);

    Orders findByIdAndUser(Long id, User user);

    List<Orders> findByRazorpayOrderId(String id);

    Page<Orders> findByStatusNot(String status, Pageable pageable);

    Integer countByStatus(String status);

    @Query("SELECT SUM(o.totalAmount) FROM Orders o")
    Integer findSumOfEarnings();

    @Query("SELECT SUM(o.totalAmount) FROM Orders o WHERE o.createdAt=:date")
    Integer findTodaysEarnings(Date date);

    @Query("SELECT o FROM Orders o WHERE o.status='placed' ORDER BY id LIMIT 8")
    List<Orders> getRecentOrders();

    @Query("SELECT o.id FROM Orders o ORDER BY o.id DESC LIMIT 1")
    Long findLastId();


}
