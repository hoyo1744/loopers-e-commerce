package com.loopers.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentType {

    POINT("POINT"),
    CARD("CARD");

    public static PaymentType from(String input) {
        for (PaymentType type : values()) {
            if (type.type.equalsIgnoreCase(input)) {
                return type;
            }
        }

        throw new IllegalArgumentException("유효하지 않은 결제 타입입니다.");
    }

    private final String type;
}
