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

    @Getter
    @Builder
    public static class PaymentDetail {
        String transactionKey;
        String orderNumber;
        CardType cardType;
        String cardNo;
        Long amount;
        String status;
        String reason;

        private PaymentDetail(String transactionKey, String orderNumber, CardType cardType, String cardNo, Long amount, String status, String reason) {
            this.transactionKey = transactionKey;
            this.orderNumber = orderNumber;
            this.cardType = cardType;
            this.cardNo = cardNo;
            this.amount = amount;
            this.status = status;
            this.reason = reason;
        }

        public static PaymentDetail of(String transactionKey, String orderNumber, CardType cardType, String cardNo, Long amount, String status, String reason) {
            return PaymentDetail.builder()
                    .transactionKey(transactionKey)
                    .orderNumber(orderNumber)
                    .cardType(cardType)
                    .cardNo(cardNo)
                    .amount(amount)
                    .status(status)
                    .reason(reason)
                    .build();
        }
    }
}
