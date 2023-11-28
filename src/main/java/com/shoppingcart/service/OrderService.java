package com.shoppingcart.service;

import com.razorpay.*;
import com.shoppingcart.dto.CreateOrderRequest;
import com.shoppingcart.dto.OrderCreatedResponse;
import com.shoppingcart.dto.OrderSuccessDto;
import com.shoppingcart.entity.Address;
import com.shoppingcart.entity.Orders;
import com.shoppingcart.repository.AddressRepository;
import com.shoppingcart.repository.OrderRepository;
import com.shoppingcart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductService productService;
    private final OrderRepository orderRepository;


    public OrderCreatedResponse createOrder(CreateOrderRequest orderRequestDto, String userEmail) {
        final String KEY_ID = "rzp_test_iggxVvLejnP8C3";
        final String KEY_SECRET = "O1a4SmQssljYKw8GHxGcAfZO";
        try {

            RazorpayClient razorpay = new RazorpayClient(KEY_ID, KEY_SECRET);
            JSONObject orderRequest = new JSONObject();

            // multiply by 100 because razorpay takes amount as paise
            Integer totalAmount = orderRequestDto.getTotalAmount() * 100;

            orderRequest.put("amount", totalAmount);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt#1");

            Order order = razorpay.orders.create(orderRequest);
            if (order == null)
                return null;

            var user = userRepository.findByEmail(userEmail).get();
            var address = addressRepository.findById(orderRequestDto.getAddressId()).get();
//            var orderAddress = Address.builder()
//                    .houseName(address.getHouseName())
//                    .locality(address.getLocality())
//                    .state(address.getState())
//                    .district(address.getDistrict())
//                    .zip(address.getZip())
//                    .build();
//            orderAddress = addressRepository.save(orderAddress);

            List<Orders> productsList = new ArrayList<>();

            for (CreateOrderRequest.ProductList product : orderRequestDto.getProducts()) {
                var orderProduct = Orders.builder()
                        .product(productService.getProductById(product.getId()))
                        .razorpayOrderId(order.get("id"))
                        .quantity(product.getQuantity())
                        .totalAmount(productService.getProductById(product.getId()).getPrice() * product.getQuantity())
                        .status("created")
                        .paymentStatus("unpaid")
                        .user(user)
                        .address(address)
                        .build();
                productsList.add(orderProduct);
            }
            orderRepository.saveAll(productsList);
            return createOrderDetails(order);

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

        return OrderCreatedResponse.builder()
                .orderId(orderId)
                .currency(currency)
                .amount(amount)
                .build();
    }



    /*
     *
     *  select list of orders with razorpayOrderId
     *  select addressId from result and get address
     *  set id of address to null and save it as new one
     *  set addressId to new addressId
     *  change other fields too.
     *
     * */

    public Object orderSuccess(OrderSuccessDto orderSuccessDto, String email) {
        var orders = orderRepository.findByRazorpayOrderId(orderSuccessDto.getRazorpayOderId());
        var address = addressRepository.findById(orderSuccessDto.getAddressId()).get();
        var user = userRepository.findByEmail(email).get();

        /*
        *  TODO
        *
        *  check for last order's addressId and currentAddressId are same
        *  if yes don't save new address
        *  select last orders address and set new order's address to last orders address
        *
        * */

        var newAddress = Address.builder()
                .houseName(address.getHouseName())
                .locality(address.getLocality())
                .state(address.getState())
                .district(address.getDistrict())
                .zip(address.getZip())

                .build();

        newAddress = addressRepository.save(newAddress);
        for (Orders order : orders) {
            order.setPaymentId(orderSuccessDto.getPaymentId());
            order.setPaymentStatus("paid");
            order.setRazorpaySignature(orderSuccessDto.getRazorpaySignature());
            order.setStatus("placed");
            order.setAddress(newAddress);
        }

        orderRepository.saveAll(orders);

        return "success";
    }
}
