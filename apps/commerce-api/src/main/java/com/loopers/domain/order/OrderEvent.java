package com.loopers.domain.order;

import lombok.*;

public class OrderEvent {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class Completed {
        private String userId;
        private Long orderId;
        private String orderNumber;
        private Long amount;

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
