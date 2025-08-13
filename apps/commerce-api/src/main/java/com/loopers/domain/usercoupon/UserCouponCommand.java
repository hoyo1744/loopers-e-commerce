package com.loopers.domain.usercoupon;

import lombok.Builder;
import lombok.Getter;

public class UserCouponCommand {

    @Getter
    @Builder
    public static class UserCoupon {
        private String userId;
        private Long couponId;

        private UserCoupon(String userId, Long couponId) {
            this.userId = userId;
            this.couponId = couponId;
        }

        public static UserCoupon of(String userId, Long couponId) {
            return UserCoupon.builder()
                    .userId(userId)
                    .couponId(couponId)
                    .build();
        }
    }
}
