package com.loopers.common.kafka.event.trace;

import com.loopers.common.kafka.event.EventType;
import com.loopers.common.kafka.topic.Topics;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

public class TraceLikeOutEvent {

    @Getter
    @Builder
    @ToString
    public static class LikeCreated implements TraceOutEvent {

        private String eventId;

        private String userId;

        private Long productId;

        private LocalDateTime eventTime;

        @Override
        public String getTopic() {
            return Topics.Trace.LIKE_CREATED;
        }

        @Override
        public String getUserId() {
            return userId;
        }

        @Override
        public String toLog() {
            return String.format("사용자 %s가 상품 %d에 대해 좋아요를 생성함", userId, productId);
        }

        @Override
        public EventType getEventType() {
            return EventType.TRACE_LIKE_CREATED;
        }

        private LikeCreated(String eventId, String userId, Long productId, LocalDateTime eventTime) {
            this.eventId = eventId;
            this.userId = userId;
            this.productId = productId;
            this.eventTime = eventTime;
        }

        public static LikeCreated of(String userId, Long productId, LocalDateTime eventTime) {
            return LikeCreated.builder()
                    .eventId(UUID.randomUUID().toString())
                    .userId(userId)
                    .productId(productId)
                    .eventTime(eventTime)
                    .build();
        }
    }

    @Getter
    @Builder
    @ToString
    public static class LikeCanceled implements TraceOutEvent {

        private String eventId;

        private String userId;

        private Long productId;

        private LocalDateTime eventTime;

        @Override
        public String getTopic() {
            return Topics.Trace.LIKE_CANCELED;
        }

        @Override
        public String getUserId() {
            return userId;
        }

        @Override
        public String toLog() {
            return String.format("사용자 %s가 상품 %d에 대한 좋아요를 취소함", userId, productId);
        }

        @Override
        public EventType getEventType() {
            return EventType.TRACE_LIKE_CANCELED;
        }

        private LikeCanceled(String eventId, String userId, Long productId, LocalDateTime eventTime) {
            this.eventId = eventId;
            this.userId = userId;
            this.productId = productId;
            this.eventTime = eventTime;
        }

        public static LikeCanceled of(String userId, Long productId, LocalDateTime eventTime) {
            return LikeCanceled.builder()
                    .eventId(UUID.randomUUID().toString())
                    .userId(userId)
                    .productId(productId)
                    .eventTime(eventTime)
                    .build();
        }
    }


}
