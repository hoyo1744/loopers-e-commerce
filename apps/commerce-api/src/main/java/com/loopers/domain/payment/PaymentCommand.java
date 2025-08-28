package com.loopers.domain.payment;

import lombok.Builder;
import lombok.Getter;

public class PaymentCommand {

    @Getter
    @Builder
    public static class Create {
        private Long amount;
        private Long orderId;
        private String orderNumber;
        private PaymentType paymentType;
        private CardType cardType;

        private Create(Long amount, Long orderId, String orderNumber, PaymentType paymentType, CardType cardType) {
            this.amount = amount;
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.paymentType = paymentType;
            this.cardType = cardType;
        }

        public static Create ofPoint(Long amount, Long orderId, String orderNumber) {
            return Create.builder()
                    .amount(amount)
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .paymentType(PaymentType.POINT)
                    .cardType(null)
                    .build();
        }

        public static Create ofCard(Long amount, Long orderId, String orderNumber, CardType cardType) {
            return Create.builder()
                    .amount(amount)
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .paymentType(PaymentType.CARD)
                    .cardType(cardType)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class Pay {
        private String userId;
        private String orderNumber;


        private Pay(String userId, String orderNumber) {
            this.userId = userId;
            this.orderNumber = orderNumber;
        }

        public static Pay of(String userId, String orderNumber) {
            return Pay.builder()
                    .userId(userId)
                    .orderNumber(orderNumber)
                    .build();
        }
    }
}
