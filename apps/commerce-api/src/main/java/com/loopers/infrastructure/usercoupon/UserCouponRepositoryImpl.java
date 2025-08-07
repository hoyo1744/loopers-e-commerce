package com.loopers.infrastructure.usercoupon;

import com.loopers.domain.usercoupon.UserCoupon;
import com.loopers.domain.usercoupon.UserCouponRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCouponRepositoryImpl implements UserCouponRepository {

    private final UserCouponJpaRepository userCouponJpaRepository;

    @Override
    public Optional<UserCoupon> findByUserIdAndCouponId(String userId, Long couponId) {
        return userCouponJpaRepository.findByUserIdAndCouponId(userId, couponId);
    }

    @Override
    public UserCoupon findById(Long userCouponId) {
        return userCouponJpaRepository.findById(userCouponId).orElseThrow( () -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자 쿠폰입니다."));
    }

    @Override
    public UserCoupon save(UserCoupon userCoupon) {
        return userCouponJpaRepository.save(userCoupon);
    }

    @Override
    public UserCoupon saveAndFlush(UserCoupon userCoupon) {
        try {
            return userCouponJpaRepository.saveAndFlush(userCoupon);
        } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
            throw new CoreException(ErrorType.CONFLICT, "이미 사용된 쿠폰입니다.");
        }
    }
}
