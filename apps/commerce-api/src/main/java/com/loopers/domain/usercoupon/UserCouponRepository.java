package com.loopers.domain.usercoupon;

import java.util.Optional;

public interface UserCouponRepository {

    Optional<UserCoupon> findByUserIdAndCouponId(String userId, Long couponId);

    UserCoupon findById(Long userCouponId);

    UserCoupon save(UserCoupon userCoupon);

    UserCoupon saveAndFlush(UserCoupon userCoupon);
}
