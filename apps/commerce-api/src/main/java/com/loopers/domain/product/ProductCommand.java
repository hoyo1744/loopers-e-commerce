package com.loopers.domain.product;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ProductCommand {

    @Getter
    @Builder
    public static class Search {
        private String userId;
        private Long brandId;
        private String sort;
        private Long page;
        private Long size;

        private Search(String userId, Long brandId, String sort, Long page, Long size) {
            this.userId = userId;
            this.brandId = brandId;
            this.sort = sort;
            this.page = page;
            this.size = size;
        }

        public static Search of(String userId, Long brandId, String sort, Long page, Long size) {
            return new Search(userId, brandId, sort, page, size);
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

        private OrderProduct(Long productId, Long quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public static OrderProduct of(Long productId, Long quantity) {
            return new OrderProduct(productId, quantity);
        }
    }

}
