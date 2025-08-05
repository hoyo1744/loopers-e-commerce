package com.loopers.interfaces.api.order;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class OrderRequest {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class Order {
        private List<OrderProduct> orderProducts;
        private Long couponId;

        public Order(List<OrderProduct> orderProducts, Long couponId) {
            this.orderProducts = orderProducts;
            this.couponId = couponId;
        }

        public static Order of(List<OrderProduct> orderItems, Long couponId) {
            return Order.builder()
                    .orderProducts(orderItems)
                    .couponId(couponId)
                    .build();
        }
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class OrderProduct {
        private Long productId;
        private Long quantity;

        public OrderProduct(Long productId, Long quantity) {
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
