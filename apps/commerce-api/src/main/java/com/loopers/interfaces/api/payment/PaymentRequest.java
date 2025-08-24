package com.loopers.interfaces.api.payment;

import com.loopers.application.payment.PaymentCriteria;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class PaymentRequest {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class TransactionInfo {
        private String transactionKey;
        private String orderId;
        private String cardType;
        private String cardNo;
        private Long amount;
        private String status;
        private String reason;

        public PaymentCriteria.PaymentResult toPaymentResult() {
            return PaymentCriteria.PaymentResult.builder()
                    .transactionKey(transactionKey)
                    .orderId(orderId)
                    .cardType(cardType)
                    .cardNo(cardNo)
                    .amount(amount)
                    .status(status)
                    .reason(reason)
                    .build();
        }
    }



}
