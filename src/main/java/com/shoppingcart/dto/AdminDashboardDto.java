package com.shoppingcart.dto;

import com.shoppingcart.entity.Product;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AdminDashboardDto {

    private Integer newOrdersCount;
    private Integer todaysEarning;
    private Integer totalEarnings;
    private List<OrderDto> recentOrders;
    private List<Product> outOfStock;

}
