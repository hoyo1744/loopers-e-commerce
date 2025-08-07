package com.loopers.domain.coupon;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponService couponService;

    @Test
    @DisplayName("쿠폰이 존재하면 해당 쿠폰을 반환한다")
    void getCoupon_shouldReturnCoupon_whenExists() {
        // given
        Long couponId = 1L;
        Coupon coupon = Coupon.create("10% 할인", 100L, 10L, DiscountType.PERCENT, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(5));

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // when
        Coupon result = couponService.getCoupon(couponId);

        // then
        Assertions.assertThat(result).isEqualTo(coupon);
        verify(couponRepository).findById(couponId);
    }

    @Test
    @DisplayName("쿠폰이 존재하지 않으면 404 Not found 예외를 발생시킨다")
    void getCoupon_should404NotFoundThrow_whenNotExists() {
        // given
        Long couponId = 1L;
        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        // when & then
        Assertions.assertThatThrownBy(() -> couponService.getCoupon(couponId))
                .isInstanceOf(CoreException.class)
                .hasMessageContaining("존재하지 않는 쿠폰 ID 입니다.")
                .extracting("errorType")
                .isEqualTo(ErrorType.NOT_FOUND);

        verify(couponRepository).findById(couponId);
    }
}
