package com.loopers.application.like;

import lombok.Builder;
import lombok.Getter;

public class LikeCriteria {

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
            return Unlike.builder()
                    .userId(userId)
                    .productId(productId)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class User {
        private String userId;

        private User(String userId) {
            this.userId = userId;
        }

        public static User of(String userId) {
            return User.builder()
                    .userId(userId)
                    .build();
        }
    }
}
