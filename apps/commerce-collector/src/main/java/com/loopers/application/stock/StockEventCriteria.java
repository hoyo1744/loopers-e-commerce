package com.loopers.application.stock;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockEventCriteria {

    @Getter
    @Builder
    public static class Adjusted {
        private String eventId;
        private String topic;
        private Integer partition;
        private Long offset;
        private LocalDateTime eventTime;
        private Long productId;
        private Long quantity;
        private Boolean isSales;

        private Adjusted(String eventId, String topic, Integer partition, Long offset, LocalDateTime eventTime, Long productId, Long quantity, Boolean isSales) {
            this.eventId = eventId;
            this.topic = topic;
            this.partition = partition;
            this.offset = offset;
            this.eventTime = eventTime;
            this.productId = productId;
            this.quantity = quantity;
            this.isSales = isSales;
        }

        public static StockEventCriteria.Adjusted of(String eventId, String topic, Integer partition, Long offset, LocalDateTime eventTime, Long productId, Long quantity, Boolean isSales) {
            return Adjusted.builder()
                    .eventId(eventId)
                    .topic(topic)
                    .partition(partition)
                    .offset(offset)
                    .eventTime(eventTime)
                    .productId(productId)
                    .quantity(quantity)
                    .isSales(isSales)
                    .build();
        }


        public LocalDate metricDate() {
            return eventTime.toLocalDate();
        }

        public Long delta() {
            return isSales ? quantity : -quantity;
        }
    }
}
