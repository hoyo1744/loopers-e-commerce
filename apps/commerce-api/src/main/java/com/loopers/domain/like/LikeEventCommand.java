package com.loopers.domain.like;

import lombok.Builder;
import lombok.Getter;

public class LikeEventCommand {

    @Getter
    @Builder
    public static class Like {
        private Long productId;
        private String userId;

        private Like(Long productId, String userId) {
            this.productId = productId;
            this.userId = userId;
        }

        public static Like of(String userId, Long productId) {
            return Like
                    .builder()
                    .userId(userId)
                    .productId(productId)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Unlike {
        private Long productId;
        private String userId;

        private Unlike(Long productId, String userId) {
            this.productId = productId;
            this.userId = userId;
        }

        public static Unlike of(String userId, Long productId) {
            return Unlike
                    .builder()
                    .userId(userId)
                    .productId(productId)
                    .build();
        }
    }
}
