package com.loopers.domain.order;

import java.util.List;

public interface OrderRepository {

    Order save(Order order);

    Order findById(Long orderId);

    List<OrderProduct> findOrderProductsByOrderId(Long orderId);


    List<Order> findByUserId(String userId);
}
