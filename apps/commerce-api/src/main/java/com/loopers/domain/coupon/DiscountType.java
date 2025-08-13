package com.loopers.domain.coupon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiscountType {
    FIXED("FIXED"),
    PERCENT("PERCENT")
    ;

    public static DiscountType from(String input) {
        for (DiscountType type : DiscountType.values()) {
            if (type.type.equalsIgnoreCase(input)) {
                return type;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 타입의 쿠폰입니다.");
    }

    private final String type;
}
