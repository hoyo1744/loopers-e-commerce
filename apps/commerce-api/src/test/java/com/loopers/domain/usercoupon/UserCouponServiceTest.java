package com.loopers.domain.usercoupon;

import com.loopers.support.error.CoreException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private UserCouponService userCouponService;

    @Nested
    @DisplayName("사용 가능 쿠폰 조회")
    class GetAvailableUserCoupon {

        @Test
        @DisplayName("사용 가능한 유저 쿠폰이면 반환한다")
        void returnsUserCoupon_whenUsable() {
            // given
            String userId = "user-1";
            Long couponId = 10L;

            UserCouponCommand.UserCoupon command = mock(UserCouponCommand.UserCoupon.class);
            when(command.getUserId()).thenReturn(userId);
            when(command.getCouponId()).thenReturn(couponId);

            UserCoupon userCoupon = mock(UserCoupon.class);
            when(userCoupon.isUsable()).thenReturn(true);

            when(userCouponRepository.findByUserIdAndCouponId(userId, couponId))
                    .thenReturn(Optional.of(userCoupon));

            // when
            UserCoupon result = userCouponService.getAvailableUserCoupon(command);

            // then
            Assertions.assertThat(result).isSameAs(userCoupon);
            verify(userCouponRepository).findByUserIdAndCouponId(userId, couponId);
            verify(userCoupon).isUsable();
            verifyNoMoreInteractions(userCouponRepository, userCoupon);
        }

        @Test
        @DisplayName("유저 쿠폰이 존재하지 않으면 404 Not found 예외를 던진다")
        void throws404NotFoundException_whenNotFound() {
            // given
            String userId = "user-1";
            Long couponId = 10L;

            UserCouponCommand.UserCoupon command = mock(UserCouponCommand.UserCoupon.class);
            when(command.getUserId()).thenReturn(userId);
            when(command.getCouponId()).thenReturn(couponId);

            when(userCouponRepository.findByUserIdAndCouponId(userId, couponId))
                    .thenReturn(Optional.empty());

            // when & then
            Assertions.assertThatThrownBy(() -> userCouponService.getAvailableUserCoupon(command))
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("userId=" + userId)
                    .hasMessageContaining("couponId=" + couponId);

            verify(userCouponRepository).findByUserIdAndCouponId(userId, couponId);
            verifyNoMoreInteractions(userCouponRepository);
        }

        @Test
        @DisplayName("유저 쿠폰이 사용 불가 상태면 409 Conflict 예외를 던진다")
        void throws409ConflictException_whenNotUsable() {
            // given
            String userId = "user-1";
            Long couponId = 10L;

            UserCouponCommand.UserCoupon command = mock(UserCouponCommand.UserCoupon.class);
            when(command.getUserId()).thenReturn(userId);
            when(command.getCouponId()).thenReturn(couponId);

            UserCoupon userCoupon = mock(UserCoupon.class);
            when(userCoupon.isUsable()).thenReturn(false);

            when(userCouponRepository.findByUserIdAndCouponId(userId, couponId))
                    .thenReturn(Optional.of(userCoupon));

            // when & then
            Assertions.assertThatThrownBy(() -> userCouponService.getAvailableUserCoupon(command))
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("사용 불가능한 상태");

            verify(userCouponRepository).findByUserIdAndCouponId(userId, couponId);
            verify(userCoupon).isUsable();
            verifyNoMoreInteractions(userCouponRepository, userCoupon);
        }
    }

    @Nested
    @DisplayName("쿠폰 사용")
    class UseUserCoupon {

        @Test
        @DisplayName("userCouponId로 조회 후 use()를 호출한다")
        void callsUse_onFetchedEntity() {
            // given
            Long userCouponId = 99L;
            UserCoupon userCoupon = mock(UserCoupon.class);

            when(userCouponRepository.findById(userCouponId)).thenReturn(userCoupon);

            // when
            userCouponService.useUserCoupon(userCouponId);

            // then
            verify(userCouponRepository).findById(userCouponId);
            verify(userCoupon).use();
            verifyNoMoreInteractions(userCouponRepository, userCoupon);
        }
    }
}
