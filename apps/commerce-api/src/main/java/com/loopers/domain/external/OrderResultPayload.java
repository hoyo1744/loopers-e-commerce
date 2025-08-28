package com.loopers.domain.external;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderResultPayload implements ExternalPayload{

    private Long orderId;
    private String orderNumber;
    private Long amount;

    private OrderResultPayload(Long orderId, String orderNumber, Long amount) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.amount = amount;
    }

    public static OrderResultPayload of(Long orderId, String orderNumber, Long amount) {
        return OrderResultPayload.builder()
                .orderId(orderId)
                .orderNumber(orderNumber)
                .amount(amount)
                .build();
    }
}
