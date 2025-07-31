package com.loopers.domain.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderInfo.Order createOrder(OrderCommand.Order command) {

        Order order = Order.create(command.getUserId(),
                OrderCommand.OrderProducts.of(
                        command.getOrderProducts().getOrderProducts().stream()
                                .map(orderProduct -> OrderCommand.OrderProduct.of(
                                        orderProduct.getProductId(),
                                        orderProduct.getQuantity(),
                                        orderProduct.getPrice()))
                                .toList()
                ));

        orderRepository.save(order);

        return OrderInfo.Order.of(
                order.getId(),
                order.getUserId(),
                order.getTotalPrice(),
                OrderInfo.OrderProducts.of(
                order.getOrderProducts().stream()
                        .map(orderProduct -> OrderInfo.OrderProduct.of(
                                orderProduct.getProductId(),
                                orderProduct.getQuantity(),
                                orderProduct.getPrice()
                                ))
                        .toList()
                ),
                order.getOrderStatus().getValue()
        );
    }

    public void updateOrderStatus(OrderCommand.OrderStatus command) {
        Order order = orderRepository.findById(command.getOrderId());
        order.updateOrderStatus(OrderStatus.from(command.getStatus()));
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
                                .map(orderProduct -> OrderInfo.OrderProduct.of(
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
                                                .map(orderProduct -> OrderInfo.OrderProduct.of(
                                                        orderProduct.getProductId(),
                                                        orderProduct.getQuantity(),
                                                        orderProduct.getPrice()))
                                                .toList()
                                ),
                                order.getOrderStatus().getValue()
                        )).toList()
        );
    }
}
