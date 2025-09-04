package com.loopers.domain.trace;

import com.loopers.common.kafka.event.EventType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class TraceLikeEvent {

    @Getter
    @Builder
    @ToString
    public static class LikeCreated implements TraceEvent {

        private String userId;

        private Long productId;

        @Override
        public EventType getEvent() {
            return EventType.TRACE_LIKE_CREATED;
        }

        @Override
        public String getUserId() {
            return userId;
        }

        @Override
        public Object getPayload() {
            return productId;
        }

        private LikeCreated(String userId, Long productId) {
            this.userId = userId;
            this.productId = productId;
        }

        public static LikeCreated of(String userId, Long productId) {
            return LikeCreated.builder()
                    .userId(userId)
                    .productId(productId)
                    .build();
        }

    }

    @Getter
    @Builder
    @ToString
    public static class LikeCanceled implements TraceEvent {

        String userId;

        Long productId;

        @Override
        public EventType getEvent() {
            return EventType.TRACE_LIKE_CANCELED;
        }

        @Override
        public String getUserId() {
            return userId;
        }

        @Override
        public Object getPayload() {
            return productId;
        }

        private LikeCanceled(String userId, Long productId) {
            this.userId = userId;
            this.productId = productId;
        }

        public static LikeCanceled of(String userId, Long productId) {
            return LikeCanceled.builder()
                    .userId(userId)
                    .productId(productId)
                    .build();
        }

    }


}
