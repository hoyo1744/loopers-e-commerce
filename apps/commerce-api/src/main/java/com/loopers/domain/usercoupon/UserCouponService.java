package com.loopers.domain.usercoupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;

    public UserCoupon getAvailableUserCoupon(UserCouponCommand.UserCoupon userCoupon) {
        UserCoupon findUserCoupon = userCouponRepository.findByUserIdAndCouponId(userCoupon.getUserId(), userCoupon.getCouponId()).orElseThrow(
                () -> new CoreException(ErrorType.NOT_FOUND,
                        String.format("userId=%s, couponId=%d 에 해당하는 유저 쿠폰이 존재하지 않습니다.", userCoupon.getUserId(), userCoupon.getCouponId())));

        if (!findUserCoupon.isUsable()) {
            throw new CoreException(ErrorType.CONFLICT,
                    String.format("userId=%s, couponId=%d → 해당 유저 쿠폰은 사용 불가능한 상태입니다.",
                            userCoupon.getUserId(), userCoupon.getCouponId())
            );
        }

        return findUserCoupon;
    }

    public void useUserCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId);
        userCoupon.use();
    }
}
