package com.loopers.domain.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardType {
    SAMSUNG("SAMSUNG"),
    KB("KB"),
    HYUNDAI("HYUNDAI"),
    ;

    public static CardType from(String input) {
        for (CardType type : values()) {
            if (type.name.equalsIgnoreCase(input)) {
                return type;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 카드 타입입니다.");
    }

    private final String name;
}
