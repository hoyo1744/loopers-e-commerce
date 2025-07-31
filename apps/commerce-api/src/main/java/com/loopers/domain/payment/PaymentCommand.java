package com.loopers.domain.payment;

import lombok.Builder;
import lombok.Getter;

public class PaymentCommand {

    @Getter
    @Builder
    public static class Pay {
        private Long amount;
        private Long orderId;

        private Pay(Long amount, Long orderId) {
            this.amount = amount;
            this.orderId = orderId;
        }

        public static PaymentCommand.Pay of(Long amount, Long orderId) {
            return PaymentCommand.Pay.builder()
                    .amount(amount)
                    .orderId(orderId)
                    .build();
        }
    }
}
