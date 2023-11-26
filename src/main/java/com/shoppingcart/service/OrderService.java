package com.shoppingcart.service;

import com.razorpay.*;
import com.shoppingcart.dto.CreateOrderRequest;
import com.shoppingcart.dto.OrderCreatedResponse;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private static final String KEY_ID = "rzp_test_iggxVvLejnP8C3";
    private static final String KEY_SECRET = "O1a4SmQssljYKw8GHxGcAfZO";


    public OrderCreatedResponse createOrder(CreateOrderRequest createOrderRequest) {
        try {

            RazorpayClient razorpay = new RazorpayClient(KEY_ID, KEY_SECRET);
            JSONObject orderRequest = new JSONObject();

            // multiply by 100 because razorpay takes amount as paise
            Integer totalAmount = createOrderRequest.getPrice() * createOrderRequest.getQuantity() * 100;
            System.out.println(totalAmount);

            orderRequest.put("amount", totalAmount);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt","receipt#1");

            JSONObject notes = new JSONObject();
            notes.put("productId", createOrderRequest.getProductId());
            notes.put("quantity", createOrderRequest.getQuantity());
            orderRequest.put("notes", notes);

            Order order = razorpay.orders.create(orderRequest);
            if(order == null)
                return null;

            var orderCreated = createOrderDetails(order);
            return orderCreated;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private OrderCreatedResponse createOrderDetails(Order order) {
        String orderId = order.get("id");
        Integer amount = order.get("amount");
        String currency = order.get("currency");

        OrderCreatedResponse res = OrderCreatedResponse.builder()
                .orderId(orderId)
                .currency(currency)
                .amount(amount)
                .build();

        return res;
    }


}
