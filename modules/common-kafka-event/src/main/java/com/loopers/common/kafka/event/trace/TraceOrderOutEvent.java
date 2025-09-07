package com.loopers.common.kafka.event.trace;

import com.loopers.common.kafka.event.EventType;
import com.loopers.common.kafka.topic.Topics;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

public class TraceOrderOutEvent {

    @Getter
    @Builder
    @ToString
    public static class OrderCompleted implements TraceOutEvent {

        private String eventId;

        private String userId;

        private Long orderId;

        private String orderNumber;

        private Long amount;

        private LocalDateTime eventTime;

        private OrderCompleted(String eventId, String userId, Long orderId, String orderNumber, Long amount, LocalDateTime eventTime) {
            this.eventId = eventId;
            this.userId = userId;
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.amount = amount;
            this.eventTime = eventTime;
        }

        public static OrderCompleted of(String userId, Long orderId, String orderNumber, Long amount, LocalDateTime eventTime) {
            return OrderCompleted.builder()
                    .eventId(UUID.randomUUID().toString())
                    .userId(userId)
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .amount(amount)
                    .eventTime(eventTime)
                    .build();
        }

        @Override
        public String getTopic() {
            return Topics.Order.COMPLETED;
        }

        @Override
        public String toLog() {
            return String.format(
                    "사용자 %s의 주문이 완료됨 (orderId=%d, orderNumber=%s, amount=%d)",
                    userId, orderId, orderNumber, amount
            );
        }

        @Override
        public EventType getEventType() {
            return EventType.TRACE_ORDER_COMPLETED;
        }
    }
}
