package com.loopers.application.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrderEventCriteria {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Order {
        private String eventId;
        private String topic;
        private Integer partition;
        private Long offset;
        private LocalDateTime eventTime;
        private Long productId;
        private Long orderId;
        private Long quantity;
        private String orderNumber;

        public static Order of(String eventId, String topic, Integer partition, Long offset, Long productId, Long orderId, String orderNumber, Long quantity, LocalDateTime eventTime) {
            return Order.builder()
                    .eventId(eventId)
                    .topic(topic)
                    .partition(partition)
                    .offset(offset)
                    .productId(productId)
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .quantity(quantity)
                    .eventTime(eventTime)
                    .build();
        }

        public LocalDate metricDate() {
            return eventTime.toLocalDate();
        }

        public Long delta() {
            return quantity;
        }
    }
}
