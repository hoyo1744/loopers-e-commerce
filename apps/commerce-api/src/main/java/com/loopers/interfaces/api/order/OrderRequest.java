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

        public Order(List<OrderProduct> orderProducts) {
            this.orderProducts = orderProducts;
        }

        public static Order of(List<OrderProduct> orderItems) {
            return Order.builder()
                    .orderProducts(orderItems)
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
