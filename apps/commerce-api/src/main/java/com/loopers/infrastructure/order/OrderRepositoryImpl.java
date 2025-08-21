package com.loopers.infrastructure.order;

import com.loopers.domain.order.Order;
import com.loopers.domain.order.OrderProduct;
import com.loopers.domain.order.OrderRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    private final OrderProductJpaRepository orderProductJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Order findById(Long orderId) {
        return orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new CoreException(ErrorType.BAD_REQUEST, "주문이 존재하지 않습니다. orderId: " + orderId));

    }

    @Override
    public Order findByOrderNumber(String orderNumber) {
        return orderJpaRepository.findByOrderNumber(orderNumber);
    }

    @Override
    public List<OrderProduct> findOrderProductsByOrderId(Long orderId) {
        return orderProductJpaRepository.findOrderProductsByOrderId(orderId);
    }

    @Override
    public List<Order> findByUserId(String userId) {
        return orderJpaRepository.findByUserId(userId);
    }
}
