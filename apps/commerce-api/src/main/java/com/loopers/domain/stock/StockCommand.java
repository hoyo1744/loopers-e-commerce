package com.loopers.domain.stock;

import com.loopers.application.order.OrderCriteria;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

public class StockCommand {

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
