package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    PENDING("PENDING"),
    COMPLETE("COMPLETE"),
    UNKWON("UKNOWN")

    ;

    public static OrderStatus from(String input) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equalsIgnoreCase(input)) {
                return status;
            }
        }
        throw new CoreException(ErrorType.BAD_REQUEST, "주문 상태를 알 수 없습니다.");
    }


    private final String value;
}
