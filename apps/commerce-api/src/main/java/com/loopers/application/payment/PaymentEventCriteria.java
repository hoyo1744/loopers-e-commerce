package com.loopers.application.payment;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentEventCommand;
import com.loopers.domain.payment.PaymentType;
import com.loopers.domain.stock.StockCommand;
import lombok.Builder;
import lombok.Getter;

public class PaymentEventCriteria {
    @Getter
    @Builder
    public static class PaymentRequest {
        private String userId;
        private Long couponId;
        private Payment payment;

        private PaymentRequest(String userId, Long couponId, Payment payment) {
            this.userId = userId;
            this.couponId = couponId;
            this.payment = payment;
        }

        public static PaymentRequest of(String userId, Long couponId, Payment payment) {
            return PaymentRequest.builder()
                    .userId(userId)
                    .couponId(couponId)
                    .payment(payment)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class PaymentCompleted {

        private Long userCouponId;
        private String orderNumber;

        private PaymentCompleted(Long userCouponId, String orderNumber) {
            this.userCouponId = userCouponId;
            this.orderNumber = orderNumber;
        }

        public static PaymentCompleted of(Long userCouponId, String orderNumber) {
            return PaymentCompleted
                    .builder()
                    .userCouponId(userCouponId)
                    .orderNumber(orderNumber)
                    .build();
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

        public static Payment of(String orderId, CardType cardType, String cardNo, Long amount, PaymentType paymentType) {
            return Payment.builder()
                    .orderNumber(orderId)
                    .cardType(cardType)
                    .cardNo(cardNo)
                    .amount(amount)
                    .paymentType(paymentType)
                    .build();
        }
    }

}
