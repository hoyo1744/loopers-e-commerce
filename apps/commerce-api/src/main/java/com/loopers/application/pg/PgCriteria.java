package com.loopers.application.pg;

import com.loopers.domain.payment.CardType;
import com.loopers.domain.pg.PgCommand;
import com.loopers.domain.usercoupon.UserCouponCommand;
import lombok.Builder;
import lombok.Getter;

public class PgCriteria {

    @Getter
    @Builder
    public static class PaymentEvent {
        private String userId;
        private Long couponId;
        private Payment payment;

        private PaymentEvent(String userId, Long couponId, Payment payment) {
            this.userId = userId;
            this.couponId = couponId;
            this.payment = payment;
        }

        public static PaymentEvent of(String userId, Long couponId, Payment payment) {
            return PaymentEvent.builder()
                    .userId(userId)
                    .couponId(couponId)
                    .payment(payment)
                    .build();
        }

        public UserCouponCommand.UserCoupon toUserCouponCommand() {
            return UserCouponCommand.UserCoupon.of(userId, couponId);
        }
    }

    @Getter
    @Builder
    public static class Payment {
        private String orderNumber;
        private CardType cardType;
        private String cardNo;
        private Long amount;
        private String callbackUrl;

        private Payment(String orderNumber, CardType cardType, String cardNo, Long amount, String callbackUrl) {
            this.orderNumber = orderNumber;
            this.cardType = cardType;
            this.cardNo = cardNo;
            this.amount = amount;
            this.callbackUrl = callbackUrl;
        }

        public static Payment of(String orderId, CardType cardType, String cardNo, Long amount, String callbackUrl) {
            return Payment.builder()
                    .orderNumber(orderId)
                    .cardType(cardType)
                    .cardNo(cardNo)
                    .amount(amount)
                    .callbackUrl(callbackUrl)
                    .build();
        }

        public PgCommand.PaymentRequest toPgCommand() {
            return PgCommand.PaymentRequest.of(orderNumber, cardType, cardNo, String.valueOf(amount), callbackUrl);
        }
    }

}
