package com.loopers.domain.like;

import lombok.Builder;
import lombok.Getter;

public class LikeCommand {

    @Getter
    @Builder
    public static class Like {
        private String userId;
        private Long productId;

        private Like(String userId, Long productId) {
            this.userId = userId;
            this.productId = productId;
        }

        public static Like of(String userId, Long productId) {
            return Like.builder()
                    .userId(userId)
                    .productId(productId)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Unlike {
        private String userId;
        private Long productId;

        private Unlike(String userId, Long productId) {
            this.userId = userId;
            this.productId = productId;
        }

        public static Unlike of(String userId, Long productId) {
            return new Unlike(userId, productId);
        }
    }

    @Getter
    @Builder
    public static class Check {
        private String userId;
        private Long productId;

        private Check(String userId, Long productId) {
            this.userId = userId;
            this.productId = productId;
        }

        public static Check of(String userId, Long productId) {
            return Check.builder()
                    .userId(userId)
                    .productId(productId)
                    .build();
        }
    }
}
