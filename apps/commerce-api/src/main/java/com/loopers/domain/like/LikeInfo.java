package com.loopers.domain.like;

import lombok.Builder;
import lombok.Getter;

public class LikeInfo {

    @Getter
    @Builder
    public static class LikeProduct {
        private String userId;
        private Long productId;

        private LikeProduct(String userId, Long productId) {
            this.userId = userId;
            this.productId = productId;
        }

        public static LikeProduct of(String userId, Long productId) {
            return LikeProduct.builder()
                    .userId(userId)
                    .productId(productId)
                    .build();
        }


    }
}
