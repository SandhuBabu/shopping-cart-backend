package com.shoppingcart.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Builder
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String razorpayOrderId;
    private String paymentId;
    private String status;
    private String paymentStatus;
    private String razorpaySignature;
    private Integer quantity;
    private Integer totalAmount;
    private Integer rating;
    private Long receiptId;

    @Temporal(TemporalType.DATE)
    private Date createdAt;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Address address;
}
