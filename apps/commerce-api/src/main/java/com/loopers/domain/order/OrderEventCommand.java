package com.loopers.domain.order;

import lombok.Builder;
import lombok.Getter;

public class OrderEventCommand {

    @Getter
    @Builder
    public static class Completed {
        private Long orderId;
        private String orderNumber;
        private Long amount;

        private Completed(Long orderId, String orderNumber, Long amount) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.amount = amount;
        }

        public static Completed of(Long orderId, String orderNumber, Long amount) {
            return Completed
                    .builder()
                    .orderId(orderId)
                    .orderNumber(orderNumber)
                    .amount(amount)
                    .build();
        }
    }
}
