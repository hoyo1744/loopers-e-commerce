package com.loopers.domain.trace;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class TraceOrderEvent {

    @Getter
    @Builder
    @ToString
    public static class OrderCompleted implements TraceEvent {

        private String userId;

        private Long orderId;

        private String orderNumber;

        private Long amount;


        @Override
        public String getEvent() {
            return "ORDER_COMPLETED";
        }

        @Override
        public String getUserId() {
            return userId;
        }

        @Override
        public Object getPayload() {
            return this.toString();
        }

        private OrderCompleted(String userId, Long orderId, String orderNumber, Long amount) {
            this.userId = userId;
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.amount = amount;
        }

        public static OrderCompleted of(String userId, Long orderId, String orderNumber, Long amount) {
            return OrderCompleted.builder()
                    .userId(userId)
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .amount(amount)
                    .build();
        }
    }
}
