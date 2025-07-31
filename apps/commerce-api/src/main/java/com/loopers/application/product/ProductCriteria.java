package com.loopers.application.product;

import lombok.Builder;
import lombok.Getter;

public class ProductCriteria {

    @Getter
    @Builder
    public static class ProductDetailRequest {
        private String userId;
        private Long productId;

        private ProductDetailRequest(String userId, Long productId) {
            this.userId = userId;
            this.productId = productId;
        }

        public static ProductDetailRequest of(String userId, Long productId) {
            return ProductDetailRequest.builder()
                    .userId(userId)
                    .productId(productId)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class ProductRequest {
        private String userId;
        private Long brandId;
        private String sort;
        private Long page;
        private Long size;

        private ProductRequest(String userId, Long brandId, String sort, Long page, Long size) {
            this.userId = userId;
            this.brandId = brandId;
            this.sort = sort;
            this.page = page;
            this.size = size;
        }

        public static ProductRequest of(String userId, Long brandId, String sort, Long page, Long size) {
            return ProductRequest.builder()
                    .userId(userId)
                    .brandId(brandId)
                    .sort(sort)
                    .page(page)
                    .size(size)
                    .build();
        }
    }

}
