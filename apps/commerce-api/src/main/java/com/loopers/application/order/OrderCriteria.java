package com.loopers.application.order;

import com.loopers.interfaces.api.order.OrderRequest;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class OrderCriteria {

    @Getter
    @Builder
    public static class Order {
        String userId;
        List<OrderProduct> orderProducts;

        private Order(String userId, List<OrderProduct> orderProducts) {
            this.userId = userId;
            this.orderProducts = orderProducts;
        }

        public static Order of(String userId, List<OrderProduct> orderProducts) {
            return Order.builder()
                    .userId(userId)
                    .orderProducts(orderProducts)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OrderProduct {
        private Long productId;
        private Long quantity;

        private OrderProduct(Long productId, Long quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, Long quantity) {
            return OrderProduct.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OrderDetail {
        private String userId;
        private Long orderId;

        private OrderDetail(String userId, Long orderId) {
            this.userId = userId;
            this.orderId = orderId;
        }

        public static OrderDetail of(String userId, Long orderId) {
            return OrderDetail.builder()
                    .userId(userId)
                    .orderId(orderId)
                    .build();
        }
    }




}
