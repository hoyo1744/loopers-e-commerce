package com.loopers.application.like;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LikeEventCriteria {

    @Getter
    @Builder
    public static class Changed {
        private String eventId;
        private String topic;
        private Integer partition;
        private Long offset;
        private LocalDateTime eventTime;
        private Long productId;
        private Boolean isLike;

        private Changed(String eventId, String topic, Integer partition, Long offset, LocalDateTime eventTime, Long productId, Boolean isLike) {
            this.eventId = eventId;
            this.topic = topic;
            this.partition = partition;
            this.offset = offset;
            this.eventTime = eventTime;
            this.productId = productId;
            this.isLike = isLike;
        }

        public static Changed of(String eventId, String topic, Integer partition, Long offset, Long productId, LocalDateTime eventTime, Boolean isLike) {
            return Changed.builder()
                    .eventId(eventId)
                    .topic(topic)
                    .partition(partition)
                    .offset(offset)
                    .productId(productId)
                    .eventTime(eventTime)
                    .isLike(isLike)
                    .build();
        }

        public Long delta() {
            return isLike ? 1L : -1L;
        }

        public LocalDate metricDate() {
            return eventTime.toLocalDate();
        }

    }
}
