package com.loopers.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(OrderCommand.Order command) {
        Order order = Order.create(command.getUserId(),
                OrderCommand.OrderProducts.of(
                        command.getOrderProducts().getOrderProducts().stream()
                                .map(orderProduct -> OrderCommand.OrderProduct.of(
                                        orderProduct.getProductId(),
                                        orderProduct.getQuantity(),
                                        orderProduct.getPrice()))
                                .toList()
                ));

        return orderRepository.save(order);
    }

    public void complete(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber);
        order.updateOrderStatus(OrderStatus.COMPLETE);
    }

    public OrderInfo.OrderDetail getOrderDetail(OrderCommand.OrderDetail command) {
        Order order = orderRepository.findById(command.getOrderId());
        List<OrderProduct> orderProductsByOrderId = orderRepository.findOrderProductsByOrderId(command.getOrderId());

        return OrderInfo.OrderDetail.of(
                order.getId(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getOrderStatus().getValue(),
                OrderInfo.OrderProducts.of(
                        orderProductsByOrderId.stream()
                                .map(orderProduct -> OrderInfo.OrderProductDto.of(
                                        orderProduct.getProductId(),
                                        orderProduct.getQuantity(),
                                        orderProduct.getPrice()))
                                .toList()
                )
        );
    }

    public OrderInfo.Orders getOrders(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);

        return OrderInfo.Orders.of(
                orders.stream()
                        .map(order -> OrderInfo.Order.of(
                                order.getId(),
                                order.getUserId(),
                                order.getTotalPrice(),
                                OrderInfo.OrderProducts.of(
                                        order.getOrderProducts().stream()
                                                .map(orderProduct -> OrderInfo.OrderProductDto.of(
                                                        orderProduct.getProductId(),
                                                        orderProduct.getQuantity(),
                                                        orderProduct.getPrice()))
                                                .toList()
                                ),
                                order.getOrderStatus().getValue()
                        )).toList()
        );
    }

    public OrderInfo.OrderProducts getOrderProducts(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber);
        return OrderInfo.OrderProducts.from(order.getOrderProducts());

    }
}
