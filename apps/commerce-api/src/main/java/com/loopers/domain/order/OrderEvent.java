package com.loopers.domain.order;

import lombok.*;

import java.util.List;

public class OrderEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Completed {
        private String userId;
        private Long orderId;
        private String orderNumber;
        private Long amount;
        private List<OrderProduct> orderProducts;

        public static Completed of(String userId, Long orderId, String orderNumber, Long amount, List<OrderProduct> orderProducts) {
            return Completed
                    .builder()
                    .userId(userId)
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .amount(amount)
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



}
