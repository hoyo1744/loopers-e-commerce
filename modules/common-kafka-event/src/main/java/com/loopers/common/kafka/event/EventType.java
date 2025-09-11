package com.loopers.common.kafka.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

    // 제품 재고 변화 이벤트
    STOCK_ADJUSTED("STOCK_ADJUSTED"),

    // 제품 좋아요 변화 이벤트
    LIKE_CHANGED("LIKE_CHANGED"),

    // 주문 이벤트
    ORDER_COMPLETED("ORDER_COMPLETED"),

    // 페이지 조회 이벤트
    PAGE_VIEWED("PAGE_VIEWED"),

    // TRACE 도메인 이벤트
    TRACE_LIKE_CREATED("TRACE_LIKE_CREATED"),
    TRACE_LIKE_CANCELED("TRACE_LIKE_CANCELED"),

    TRACE_ORDER_COMPLETED("TRACE_ORDER_COMPLETED"),

    TRACE_PAYMENT_COMPLETED("TRACE_PAYMENT_COMPLETED")
    ;

    public static EventType from(String name) {
        for (EventType type : EventType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown EventType: " + name);
    }

    private final String name;
}
