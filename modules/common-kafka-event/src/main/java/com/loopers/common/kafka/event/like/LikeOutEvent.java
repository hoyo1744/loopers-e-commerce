package com.loopers.common.kafka.event.like;

import com.loopers.common.kafka.event.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class LikeOutEvent {

    @Getter
    @Builder
    public static class Changed implements Event {
        private String eventId;
        private String userId;
        private Long productId;
        private boolean liked;
        private LocalDateTime eventTime;

        private Changed(String eventId, String userId, Long productId, boolean liked, LocalDateTime eventTime) {
            this.eventId = eventId;
            this.userId = userId;
            this.productId = productId;
            this.liked = liked;
            this.eventTime = eventTime;
        }

        public static Changed of(String userId, Long productId, boolean liked, LocalDateTime eventTime) {
            return Changed.builder()
                    .eventId(java.util.UUID.randomUUID().toString())
                    .userId(userId)
                    .productId(productId)
                    .liked(liked)
                    .eventTime(eventTime)
                    .build();
        }
    }
}
