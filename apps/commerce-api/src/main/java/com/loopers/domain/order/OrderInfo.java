package com.loopers.domain.order;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class OrderInfo {

    @Getter
    @Builder
    public static class OrderDetail {
        Long orderId;
        String userId;
        Long totalPrice;
        String orderStatus;
        OrderProducts orderProducts;

        private OrderDetail(Long orderId, String userId, Long totalPrice, String orderStatus, OrderProducts orderProducts) {
            this.orderId = orderId;
            this.userId = userId;
            this.totalPrice = totalPrice;
            this.orderStatus = orderStatus;
            this.orderProducts = orderProducts;
        }

        public static OrderDetail of(Long orderId, String userId, Long totalPrice, String orderStatus, OrderProducts orderProducts) {
            return OrderDetail.builder()
                    .orderId(orderId)
                    .userId(userId)
                    .totalPrice(totalPrice)
                    .orderStatus(orderStatus)
                    .orderProducts(orderProducts)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Orders {
        private List<Order> orders;

        private Orders(List<Order> orders) {
            this.orders = orders;
        }

        public static Orders of(List<Order> orders) {
            return Orders.builder()
                    .orders(orders)
                    .build();
        }
    }


    @Getter
    @Builder
    public static class Order {
        Long orderId;
        String userId;
        Long totalPrice;
        OrderProducts orderProducts;
        String orderStatus;

        private Order(Long orderId, String userId, Long totalPrice, OrderProducts orderProducts, String orderStatus) {
            this.orderId = orderId;
            this.userId = userId;
            this.totalPrice = totalPrice;
            this.orderProducts = orderProducts;
            this.orderStatus = orderStatus;
        }

        public static Order of(Long oderId, String userId, Long totalPrice, OrderProducts orderProducts, String orderStatus) {
            return Order.builder()
                    .orderId(oderId)
                    .userId(userId)
                    .totalPrice(totalPrice)
                    .orderProducts(orderProducts)
                    .orderStatus(orderStatus)
                    .build();
        }
    }


    @Getter
    @Builder
    public static class OrderProducts {
        private List<OrderProduct> orderProducts;

        private OrderProducts(List<OrderProduct> orderProducts) {
            this.orderProducts = orderProducts;
        }

        public static OrderProducts of(List<OrderProduct> orderProducts) {
            return OrderProducts.builder()
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

        public static OrderProduct of(Long productId, Long quantity, Long price) {
            return OrderProduct.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .price(price)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Payment {
        private String paymentStatus;

        private Payment(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public static Payment of(String paymentStatus) {
            return Payment.builder()
                    .paymentStatus(paymentStatus)
                    .build();
        }

    }
}
