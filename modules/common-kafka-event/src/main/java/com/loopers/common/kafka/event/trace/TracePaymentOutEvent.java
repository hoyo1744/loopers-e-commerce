package com.loopers.common.kafka.event.trace;

import com.loopers.common.kafka.event.EventType;
import com.loopers.common.kafka.topic.Topics;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

public class TracePaymentOutEvent {

    @Getter
    @Builder
    @ToString
    public static class PaymentCompleted implements TraceOutEvent {

        private String eventId;

        private String userId;

        private Long orderId;

        private String orderNumber;

        private Long paymentId;

        private Long amount;

        private LocalDateTime eventTime;


        private PaymentCompleted(String eventId, String userId, Long orderId, String orderNumber, Long paymentId, Long amount, LocalDateTime eventTime) {
            this.eventId = eventId;
            this.userId = userId;
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.paymentId = paymentId;
            this.amount = amount;
            this.eventTime = eventTime;
        }

        public static PaymentCompleted of(String userId, Long orderId, String orderNumber, Long paymentId, Long amount, LocalDateTime eventTime) {
            return PaymentCompleted.builder()
                    .eventId(UUID.randomUUID().toString())
                    .userId(userId)
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .paymentId(paymentId)
                    .amount(amount)
                    .eventTime(eventTime)
                    .build();
        }

        @Override
        public String getTopic() {
            return Topics.Trace.PAYMENT_COMPLETED;
        }

        @Override
        public String toLog() {
            return String.format(
                    "사용자 %s의 결제가 완료됨 (orderId=%d, orderNumber=%s, paymentId=%d, amount=%d)",
                    userId, orderId, orderNumber, paymentId, amount
            );
        }

        @Override
        public EventType getEventType() {
            return EventType.TRACE_PAYMENT_COMPLETED;
        }
    }
}
