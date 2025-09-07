package com.loopers.domain.payment;

import lombok.Builder;
import lombok.Getter;

public class PaymentEventCommand {
    @Getter
    @Builder
    public static class Requested {
        private String userId;
        private Long couponId;
        private Payment payment;

        private Requested(String userId, Long couponId, Payment payment) {
            this.userId = userId;
            this.couponId = couponId;
            this.payment = payment;
        }

        public static Requested of(String userId, Long couponId , Payment payment) {
            return Requested.builder()
                    .userId(userId)
                    .couponId(couponId)
                    .payment(payment)
                    .build();
        }

        public PaymentEvent.Request toPaymentRequestedEvent() {
            return PaymentEvent.Request.of(
                    userId, couponId, PaymentEvent.Payment.of(
                            payment.getOrderNumber(),
                            payment.getCardType(),
                            payment.getCardNo(),
                            payment.getAmount(),
                            payment.getPaymentType()));
        }
    }

    @Getter
    @Builder
    public static class Completed {

        private Long userCouponId;

        private String orderNumber;

        private Completed(Long userCouponId, String orderNumber) {
            this.userCouponId = userCouponId;
            this.orderNumber = orderNumber;
        }

        public static Completed of(Long userCouponId, String orderNumber) {
            return Completed
                    .builder()
                    .userCouponId(userCouponId)
                    .orderNumber(orderNumber)
                    .build();
        }

        public PaymentEvent.Completed toPaymentCompletedEvent() {
            return PaymentEvent.Completed.of(userCouponId, orderNumber);
        }
    }



    @Getter
    @Builder
    public static class Payment {
        private String orderNumber;
        private CardType cardType;
        private String cardNo;
        private Long amount;
        private PaymentType paymentType;

        private Payment(String orderNumber, CardType cardType, String cardNo, Long amount, PaymentType paymentType) {
            this.orderNumber = orderNumber;
            this.cardType = cardType;
            this.cardNo = cardNo;
            this.amount = amount;
            this.paymentType = paymentType;
        }

        public static Payment of(String orderNumber, CardType cardType, String cardNo, Long amount, PaymentType paymentType) {
            return Payment.builder()
                    .orderNumber(orderNumber)
                    .cardType(cardType)
                    .cardNo(cardNo)
                    .amount(amount)
                    .paymentType(paymentType)
                    .build();
        }
    }
}
