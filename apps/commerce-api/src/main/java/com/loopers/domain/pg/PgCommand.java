package com.loopers.domain.pg;

import com.loopers.domain.payment.CardType;
import lombok.Builder;
import lombok.Getter;

public class PgCommand {

    @Getter
    @Builder
    public static class PaymentRequest {
        private String orderId;
        private CardType cardType;
        private String cardNo;
        private String amount;
        private String callbackUrl;

        private PaymentRequest(String orderId, CardType cardType, String cardNo, String amount, String callbackUrl) {
            this.orderId = orderId;
            this.cardType = cardType;
            this.cardNo = cardNo;
            this.amount = amount;
            this.callbackUrl = callbackUrl;
        }

        public static PaymentRequest of(String orderId, CardType cardType, String cardNo, String amount, String callbackUrl) {
            return PaymentRequest.builder()
                    .orderId(orderId)
                    .cardType(cardType)
                    .cardNo(cardNo)
                    .amount(amount)
                    .callbackUrl(callbackUrl)
                    .build();
        }
    }

}
