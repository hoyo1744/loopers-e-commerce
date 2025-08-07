package com.loopers.domain.usercoupon;

import lombok.Builder;
import lombok.Getter;

public class UserCouponInfo {

    @Getter
    @Builder
    public static class AvailableCoupon {
        private final Long userCouponId;

        private AvailableCoupon(Long userCouponId) {
            this.userCouponId = userCouponId;
        }

        public static AvailableCoupon of(Long userCouponId) {
            return AvailableCoupon.builder().userCouponId(userCouponId).build();
        }
    }
}
