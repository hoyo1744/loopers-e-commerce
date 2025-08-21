package com.loopers.interfaces.api.order;


import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderResult;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderV1Controller implements OrderV1ApiSpec{

    private final OrderFacade orderFacade;


    @PostMapping
    public ApiResponse<Void> order(
            @RequestHeader(value = "X-USER-ID", required = true) String userId,
            @RequestBody OrderRequest.Order order) {

        orderFacade.order(order.toOrderCriteria(userId));

        return ApiResponse.success();
    }


    @GetMapping
    public ApiResponse<OrderResponse.Orders> getOrders(
            @RequestHeader(value = "X-USER-ID", required = true) String userId) {

        OrderResult.Orders orders = orderFacade.getOrders(userId);

        return ApiResponse.success(OrderResponse.Orders.of(
                orders.getOrders().stream()
                        .map(o -> OrderResponse.Order.of(
                                o.getOrderStatus(),
                                o.getTotalPrice(),
                                o.getProducts().stream()
                                        .map(op -> OrderResponse.Product.of(
                                                op.getName(),
                                                op.getPrice(),
                                                op.getBrand()))
                                        .collect(Collectors.toList())
                        )).collect(Collectors.toList())
        ));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse.Order> getOrder(
            @RequestHeader(value = "X-USER-ID", required = true) String userId,
            @PathVariable Long orderId) {
        OrderResult.Order order = orderFacade.getOrder(OrderCriteria.OrderDetail.of(userId, orderId));

        return ApiResponse.success(OrderResponse.Order.of(
                order.getOrderStatus(),
                order.getTotalPrice(),
                order.getProducts().stream()
                        .map(op -> OrderResponse.Product.of(
                                op.getName(),
                                op.getPrice(),
                                op.getBrand()
                        )
                        ).collect(Collectors.toList()
        )));
    }




}
