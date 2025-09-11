package com.loopers.common.kafka.event.pageview;

import com.loopers.common.kafka.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class PageViewOutEvent {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Viewed implements Event {
        private String eventId;
        private Long productId;
        private LocalDateTime eventTime;

        public static Viewed of(Long productId, LocalDateTime eventTime) {
            return Viewed.builder()
                    .eventId(java.util.UUID.randomUUID().toString())
                    .productId(productId)
                    .eventTime(eventTime)
                    .build();
        }
    }
}
