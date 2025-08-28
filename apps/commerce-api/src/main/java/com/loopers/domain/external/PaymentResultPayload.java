package com.loopers.domain.external;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResultPayload implements ExternalPayload{
    private Long userCouponId;
    private String orderNumber;

    private PaymentResultPayload(Long userCouponId, String orderNumber) {
        this.userCouponId = userCouponId;
        this.orderNumber = orderNumber;
    }

    public static PaymentResultPayload of(Long userCouponId, String orderNumber) {
        return PaymentResultPayload
                .builder()
                .userCouponId(userCouponId)
                .orderNumber(orderNumber)
                .build();
    }
}
