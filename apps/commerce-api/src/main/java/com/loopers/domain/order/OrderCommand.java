package com.loopers.domain.order;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class OrderCommand {

    @Getter
    @Builder
    public static class Order {
        String userId;
        OrderProducts orderProducts;

        private Order(String userId, OrderProducts orderProducts) {
            this.userId = userId;
            this.orderProducts = orderProducts;
        }

        public static OrderCommand.Order of(String userId, OrderProducts orderProducts) {
            return OrderCommand.Order.builder()
                    .userId(userId)
                    .orderProducts(orderProducts)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OrderStatus {
        private Long orderId;
        private String status;

        private OrderStatus(Long orderId, String status) {
            this.orderId = orderId;
            this.status = status;
        }

        public static OrderCommand.OrderStatus of(Long orderId, String status) {
            return OrderStatus.builder()
                    .orderId(orderId)
                    .status(status)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OrderProducts {
        private List<OrderCommand.OrderProduct> orderProducts;

        private OrderProducts(List<OrderCommand.OrderProduct> orderProducts) {
            this.orderProducts = orderProducts;
        }

        public static OrderCommand.OrderProducts of(List<OrderCommand.OrderProduct> orderProducts) {
            return OrderCommand.OrderProducts.builder()
                    .orderProducts(orderProducts)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OrderProduct {
        private Long productId;
        private Long quantity;
        private Long price;

        private OrderProduct(Long productId, Long quantity, Long price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        public static OrderCommand.OrderProduct of(Long productId, Long quantity, Long price) {
            return OrderProduct.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .price(price)
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

        public static OrderCommand.OrderDetail of(String userId, Long orderId) {
            return OrderDetail.builder()
                    .userId(userId)
                    .orderId(orderId)
                    .build();
        }


    }
}
