package com.loopers.domain.payment;

import lombok.Builder;
import lombok.Getter;

public class PaymentCommand {

    @Getter
    @Builder
    public static class Pay {
        private Long amount;
        private Long orderId;
        private String orderNumber;
        private PaymentType paymentType;
        private CardType cardType;

        private Pay(Long amount, Long orderId, String orderNumber, PaymentType paymentType, CardType cardType) {
            this.amount = amount;
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.paymentType = paymentType;
            this.cardType = cardType;
        }

        public static PaymentCommand.Pay ofPoint(Long amount, Long orderId, String orderNumber) {
            return Pay.builder()
                    .amount(amount)
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .paymentType(PaymentType.POINT)
                    .cardType(null)
                    .build();
        }

        public static PaymentCommand.Pay ofCard(Long amount, Long orderId, String orderNumber, CardType cardType) {
            return Pay.builder()
                    .amount(amount)
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .paymentType(PaymentType.CARD)
                    .cardType(cardType)
                    .build();
        }
    }
}
