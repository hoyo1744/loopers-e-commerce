package com.loopers.domain.payment;

import com.loopers.application.payment.PaymentEventCriteria;
import lombok.Builder;
import lombok.Getter;

public class PaymentEvent {

    @Getter
    @Builder
    public static class Request {
        private String userId;
        private Long couponId;
        private Payment payment;

        private Request(String userId, Long couponId, Payment payment) {
            this.userId = userId;
            this.couponId = couponId;
            this.payment = payment;
        }

        public static Request of(String userId, Long couponId, Payment payment) {
            return Request.builder()
                    .userId(userId)
                    .couponId(couponId)
                    .payment(payment)
                    .build();
        }

        public PaymentEventCriteria.PaymentRequest toPaymentRequestEvent() {
            return PaymentEventCriteria.PaymentRequest.of(
                    userId,
                    couponId,
                    PaymentEventCriteria.Payment.of(
                            payment.orderId,
                            payment.cardType,
                            payment.cardNo,
                            payment.amount,
                            payment.getPaymentType()
                    )
            );
        }
    }

    @Getter
    @Builder
    public static class Completed {
        private Long userCouponId;
        private String orderNumber;

        public static Completed of(Long userCouponId, String orderNumber) {
            return Completed
                    .builder()
                    .userCouponId(userCouponId)
                    .orderNumber(orderNumber)
                    .build();
        }
    }


    @Getter
    @Builder
    public static class Payment {
        private String orderId;
        private CardType cardType;
        private String cardNo;
        private Long amount;
        private PaymentType paymentType;

        private Payment(String orderId, CardType cardType, String cardNo, Long amount, PaymentType paymentType) {
            this.orderId = orderId;
            this.cardType = cardType;
            this.cardNo = cardNo;
            this.amount = amount;
            this.paymentType = paymentType;
        }

        public static Payment of(String orderId, CardType cardType, String cardNo, Long amount, PaymentType paymentType) {
            return Payment.builder()
                    .orderId(orderId)
                    .cardType(cardType)
                    .cardNo(cardNo)
                    .amount(amount)
                    .paymentType(paymentType)
                    .build();
        }
    }

}
