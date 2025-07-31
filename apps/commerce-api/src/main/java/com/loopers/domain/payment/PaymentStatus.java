package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {

    READY("READY"),
    PAID("PAID"),
    CANCEL("CANCEL")
    ;

    public static PaymentStatus from(String input) {
        for (PaymentStatus status : values()) {
            if (status.value.equalsIgnoreCase(input)) {
                return status;
            }
        }
        throw new CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 결제 상태입니다: " + input);
    }

    private final String value;

}
