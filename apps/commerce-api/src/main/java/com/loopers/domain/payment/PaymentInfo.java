package com.loopers.domain.payment;

import lombok.Builder;
import lombok.Getter;

public class PaymentInfo {

    @Getter
    @Builder
    public static class Payment {

        private Long paymentId;

        private Long amount;

        private Long orderId;

        private String paymentStatus;

        private Payment(Long paymentId, Long amount, Long orderId, String paymentStatus) {
            this.paymentId = paymentId;
            this.amount = amount;
            this.orderId = orderId;
            this.paymentStatus = paymentStatus;
        }

        public static Payment of(Long paymentId, Long amount, Long orderId, String paymentStatus) {
            return  Payment.builder().paymentId(paymentId).amount(amount).orderId(orderId).paymentStatus(paymentStatus).build();
        }
    }
}
