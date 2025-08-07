package com.loopers.domain.usercoupon;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserCouponTest {

    @Nested
    @DisplayName("쿠폰 생성 테스트")
    class Create {

        @Test
        @DisplayName("정상적으로 유저 쿠폰을 생성한다")
        void createUserCouponSuccessfully() {
            // given
            String userId = "user1";
            Long couponId = 1L;

            // when
            UserCoupon userCoupon = UserCoupon.create(userId, couponId);

            // then
            assertThat(userCoupon.getUserId()).isEqualTo(userId);
            assertThat(userCoupon.getCouponId()).isEqualTo(couponId);
            assertThat(userCoupon.getIssuedAt()).isNotNull();
            assertThat(userCoupon.getUserCouponStatus()).isEqualTo(UserCouponStatus.NO_USED);
            assertThat(userCoupon.getUsedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("쿠폰 사용 가능 여부 테스트")
    class IsUsable {

        @Test
        @DisplayName("상태가 NO_USED이면 true를 반환한다")
        void isUsable_whenStatusIsNoUsed() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId("user1")
                    .couponId(1L)
                    .issuedAt(LocalDateTime.now())
                    .userCouponStatus(UserCouponStatus.NO_USED)
                    .build();

            assertThat(userCoupon.isUsable()).isTrue();
        }

        @Test
        @DisplayName("상태가 USED이면 false를 반환한다")
        void isUsable_whenStatusIsUsed() {
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId("user1")
                    .couponId(1L)
                    .issuedAt(LocalDateTime.now())
                    .userCouponStatus(UserCouponStatus.USED)
                    .build();

            assertThat(userCoupon.isUsable()).isFalse();
        }
    }

    @Nested
    @DisplayName("쿠폰 사용 테스트")
    class Use {

        @Test
        @DisplayName("NO_USED 상태의 쿠폰을 사용하면 상태가 USED로 변경된다")
        void useCouponSuccessfully() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId("user1")
                    .couponId(1L)
                    .issuedAt(LocalDateTime.now())
                    .userCouponStatus(UserCouponStatus.NO_USED)
                    .build();

            // when
            userCoupon.use();

            // then
            assertThat(userCoupon.getUserCouponStatus()).isEqualTo(UserCouponStatus.USED);
            assertThat(userCoupon.getUsedAt()).isNotNull();
        }

        @Test
        @DisplayName("USED 상태의 쿠폰을 사용하면 400 Bad Request 예외가 발생한다")
        void useCoupon_shouldThrow_whenAlreadyUsed() {
            // given
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId("user1")
                    .couponId(1L)
                    .issuedAt(LocalDateTime.now())
                    .usedAt(LocalDateTime.now().minusDays(1))
                    .userCouponStatus(UserCouponStatus.USED)
                    .build();

            // when & then
            assertThatThrownBy(userCoupon::use)
                    .isInstanceOf(CoreException.class)
                    .hasMessageContaining("사용할 수 없는 쿠폰");
        }
    }
}
