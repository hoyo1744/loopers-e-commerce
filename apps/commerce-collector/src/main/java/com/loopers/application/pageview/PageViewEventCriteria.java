package com.loopers.application.pageview;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PageViewEventCriteria {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class View {
        private String eventId;
        private String topic;
        private Integer partition;
        private Long offset;
        private Long productId;
        private LocalDateTime eventTime;

        public static View of(String eventId, String topic, Integer partition, Long offset, Long productId, LocalDateTime eventTime) {
            return View.builder()
                    .eventId(eventId)
                    .topic(topic)
                    .partition(partition)
                    .offset(offset)
                    .productId(productId)
                    .eventTime(eventTime)
                    .build();
        }

        public LocalDate metricDate() {
            return eventTime.toLocalDate();
        }

        public Long delta() {
            return 1L;
        }
    }
}
