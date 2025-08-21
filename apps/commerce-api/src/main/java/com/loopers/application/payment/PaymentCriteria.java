package com.loopers.application.payment;

import lombok.Builder;
import lombok.Getter;

public class PaymentCriteria {

    @Getter
    @Builder
    public static class PaymentResult {
        private String transactionKey;
        private String orderId;
        private String cardType;
        private String cardNo;
        private Long amount;
        private String status;
        private String reason;

        private PaymentResult(String transactionKey, String orderId, String cardType, String cardNo, Long amount, String status, String reason) {
            this.transactionKey = transactionKey;
            this.orderId = orderId;
            this.cardType = cardType;
            this.cardNo = cardNo;
            this.amount = amount;
            this.status = status;
            this.reason = reason;
        }

    }
}
