package com.loopers.domain.like;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class LikeEvent {

    @Getter
    @ToString
    @Builder
    public static class Like {
        private Long productId;
        private String userId;

        private Like(Long productId, String userId) {
            this.productId = productId;
            this.userId = userId;
        }

        public static Like of(Long productId, String userId) {
            return Like
                    .builder()
                    .productId(productId)
                    .userId(userId)
                    .build();
        }
    }

    @Getter
    @ToString
    @Builder
    public static class Unlike {
        private Long productId;
        private String userId;

        private Unlike(Long productId, String userId) {
            this.productId = productId;
            this.userId = userId;
        }

        public static Unlike of(Long productId, String userId) {
            return Unlike
                    .builder()
                    .productId(productId)
                    .userId(userId)
                    .build();
        }
    }


}
