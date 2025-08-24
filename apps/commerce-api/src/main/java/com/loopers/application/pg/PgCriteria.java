package com.loopers.application.pg;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.pg.PgCommand;
import lombok.Builder;
import lombok.Getter;

public class PgCriteria {

    @Getter
    @Builder
    public static class PaymentEvent {
        private String userId;
        private Payment payment;

        private PaymentEvent(String userId, Payment payment) {
            this.userId = userId;
            this.payment = payment;
        }

        public static PaymentEvent of(String userId, Payment payment) {
            return PaymentEvent.builder()
                    .userId(userId)
                    .payment(payment)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Payment {
        private String orderId;
        private CardType cardType;
        private String cardNo;
        private String amount;
        private String callbackUrl;

        private Payment(String orderId, CardType cardType, String cardNo, String amount, String callbackUrl) {
            this.orderId = orderId;
            this.cardType = cardType;
            this.cardNo = cardNo;
            this.amount = amount;
            this.callbackUrl = callbackUrl;
        }

        public static Payment of(String orderId, CardType cardType, String cardNo, String amount, String callbackUrl) {
            return Payment.builder()
                    .orderId(orderId)
                    .cardType(cardType)
                    .cardNo(cardNo)
                    .amount(amount)
                    .callbackUrl(callbackUrl)
                    .build();
        }

        public PgCommand.PaymentRequest toPgCommand() {
            return PgCommand.PaymentRequest.of(orderId, cardType, cardNo, amount, callbackUrl);
        }
    }

}
