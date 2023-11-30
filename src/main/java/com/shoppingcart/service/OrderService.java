package com.shoppingcart.service;

import com.razorpay.*;
import com.shoppingcart.dto.*;
import com.shoppingcart.entity.Address;
import com.shoppingcart.entity.Orders;
import com.shoppingcart.entity.User;
import com.shoppingcart.repository.AddressRepository;
import com.shoppingcart.repository.OrderRepository;
import com.shoppingcart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ProductService productService;
    private final OrderRepository orderRepository;

    private final Integer DELIVERY_FEE = 49;

    public OrderCreatedResponse createOrder(CreateOrderRequest orderRequestDto, String userEmail) {

        try {
            Integer totalAmount = (orderRequestDto.getTotalAmount() + DELIVERY_FEE) * 100;
            Order order = createRazorpayOrder(totalAmount);
            if (order == null)
                return null;

            var user = userRepository.findByEmail(userEmail).get();
            var address = user.getAddress();
            var orderAddress = Address.builder()
                    .houseName(address.getHouseName())
                    .locality(address.getLocality())
                    .district(address.getDistrict())
                    .state(address.getState())
                    .zip(address.getZip())
                    .build();
            orderAddress = addressRepository.save(orderAddress);

            var productsList = createOrdersList(orderRequestDto, order, user, orderAddress);
            orderRepository.saveAll(productsList);
            return createOrderDetails(order);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }



    public String paymentSuccess(OrderSuccessDto orderSuccessDto) {
        try {
            var orders = orderRepository.findByRazorpayOrderId(orderSuccessDto.getRazorpayOderId());
            for (Orders order : orders) {
                order.setPaymentId(orderSuccessDto.getPaymentId());
                order.setPaymentStatus("paid");
                order.setRazorpaySignature(orderSuccessDto.getRazorpaySignature());
                order.setCreateAt(new Date(System.currentTimeMillis()));
                order.setStatus("placed");
            }

            orderRepository.saveAll(orders);
            return "success";
        } catch (Exception e) {
            return null;
        }
    }

    public String paymentFailed(PaymentFailureDto paymentFailureDto) {
        var orders = orderRepository.findByRazorpayOrderId(paymentFailureDto.getOrderId());
        for(Orders order: orders) {
            order.setPaymentStatus("failed");
            order.setPaymentId(paymentFailureDto.getPaymentId());
        }
        orderRepository.saveAll(orders);
        return "saved payment failure";
    }

    public PaginationResponse<OrderDto> getAllOrdersForAdmin(Integer pageSize, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        var orders = orderRepository.findByStatusNot("created", pageable);
        var res = buildOrdersPaginated(orders);
        return res;
    }

    public Integer getCountOfNewOrders(String status) {
        return orderRepository.countByStatus(status);
    }

    public String changeStatus(Long orderId, String status) {
        try {
            var order = orderRepository.findById(orderId).get();
            order.setStatus(status);
            orderRepository.save(order);
            return "Status successfully changed to "+status;
        } catch (Exception e) {
            return "Failed to change status to "+status;
        }
    }

    private Order createRazorpayOrder(Integer totalAmount) throws RazorpayException {
        final String KEY_ID = "rzp_test_iggxVvLejnP8C3";
        final String KEY_SECRET = "O1a4SmQssljYKw8GHxGcAfZO";

        RazorpayClient razorpay = new RazorpayClient(KEY_ID, KEY_SECRET);
        JSONObject orderRequest = new JSONObject();


        orderRequest.put("amount", totalAmount);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt#1");

        return razorpay.orders.create(orderRequest);
    }

    private List<Orders> createOrdersList(CreateOrderRequest orderRequestDto, Order order, User user, Address orderAddress) {
        List<Orders> productsList = new ArrayList<>();
        for (CreateOrderRequest.ProductList product : orderRequestDto.getProducts()) {
            var orderProduct = Orders.builder()
                    .product(productService.getProductById(product.getId()))
                    .razorpayOrderId(order.get("id"))
                    .quantity(product.getQuantity())
                    .totalAmount((productService.getProductById(product.getId()).getPrice() * product.getQuantity()) + DELIVERY_FEE)
                    .status("created")
                    .paymentStatus("unpaid")
                    .user(user)
                    .address(orderAddress)
                    .build();
            productsList.add(orderProduct);
        }
        return productsList;
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

    private List<OrderDto> buildOrderDto(List<Orders> orders) {
        List<OrderDto> result = new ArrayList<>();
        for(Orders order: orders) {
            var dto = OrderDto.builder()
                    .id(order.getId())
                    .createdAt(order.getCreateAt())
                    .razorpayOrderId(order.getRazorpayOrderId())
                    .paymentId(order.getPaymentId())
                    .status(order.getStatus())
                    .paymentStatus(order.getPaymentStatus())
                    .razorpaySignature(order.getRazorpaySignature())
                    .quantity(order.getQuantity())
                    .totalAmount(order.getTotalAmount())
                    .fullName(order.getUser().getFullName())
                    .email(order.getUser().getEmail())
                    .mobile(order.getUser().getMobile())
                    .product(order.getProduct())
                    .address(order.getAddress())
                    .build();
            result.add(dto);
        }
//        Collections.reverse(result);
        return result;
    }

    private PaginationResponse<OrderDto> buildOrdersPaginated(Page<Orders> orders) {
        // create paginated response set content by building orderdto
        PaginationResponse<OrderDto> response = new PaginationResponse<>();
        response.setTotalPages(orders.getTotalPages());
        response.setFirst(orders.isFirst());
        response.setLast(orders.isLast());
        response.setEmpty(orders.isEmpty());
        response.setPageNo(orders.getNumber()+1);
        response.setContent(buildOrderDto(orders.getContent()));
        return response;
    }
}
