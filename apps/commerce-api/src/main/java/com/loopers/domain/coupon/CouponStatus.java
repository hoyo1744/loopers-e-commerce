package com.loopers.domain.coupon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    EXPIRED("EXPIRED"),
    DELETE("DELETE")
    ;

    public void validateUsable() {
        if (!isUsable()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }
    }

    public boolean isUsable() {
        return this == ACTIVE;
    }


    private final String status;
}
