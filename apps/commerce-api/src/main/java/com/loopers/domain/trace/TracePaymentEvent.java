package com.loopers.domain.trace;

import com.loopers.domain.payment.PaymentType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class TracePaymentEvent {

    @Getter
    @Builder
    @ToString
    public static class PaymentCompleted implements TraceEvent {

        private String userId;

        private Long orderId;

        private String orderNumber;

        private Long paymentId;

        private Long amount;


        @Override
        public String getEvent() {
            return "PAYMENT_COMPLETED";
        }

        @Override
        public String getUserId() {
            return userId;
        }

        @Override
        public Object getPayload() {
            return this.toString();
        }

        private PaymentCompleted(String userId, Long orderId, String orderNumber, Long paymentId, Long amount) {
            this.userId = userId;
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.paymentId = paymentId;
            this.amount = amount;
        }

        public static PaymentCompleted of(String userId, Long orderId, String orderNumber, Long paymentId, Long amount) {
            return PaymentCompleted.builder()
                    .userId(userId)
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .paymentId(paymentId)
                    .amount(amount)
                    .build();
        }
    }
}
