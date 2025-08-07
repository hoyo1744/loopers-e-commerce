package com.loopers.domain.usercoupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserCouponStatus {
    USED("USED"),
    NO_USED("NO_USED"),
    EXPIRED("EXPIRED")
    ;

    public boolean isUsable() {
        return this == NO_USED;
    }

    public void validateUsable() {
        if (!isUsable()) {
            throw new CoreException(ErrorType.CONFLICT, "이미 사용했거나 사용할 수 없는 쿠폰입니다.");
        }
    }

    private final String status;
}
