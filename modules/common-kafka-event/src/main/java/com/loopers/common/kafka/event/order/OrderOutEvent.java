package com.loopers.common.kafka.event.order;

import com.loopers.common.kafka.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class OrderOutEvent {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Order implements Event {
        private String eventId;
        private String userId;
        private Long productId;
        private Long orderId;
        private String orderNumber;
        private Long quantity;
        private LocalDateTime eventTime;

        public static Order of(String userId, Long productId, Long orderId, String orderNumber, Long quantity, LocalDateTime eventTime) {
            return Order.builder()
                    .eventId(java.util.UUID.randomUUID().toString())
                    .userId(userId)
                    .productId(productId)
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .quantity(quantity)
                    .eventTime(eventTime)
                    .build();
        }

    }
}
