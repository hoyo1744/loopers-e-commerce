package com.loopers.domain.coupon;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class CouponTest {

    @Nested
    @DisplayName("쿠폰 생성 테스트")
    class Create {

        @Test
        @DisplayName("정상적으로 쿠폰을 생성한다")
        void createCouponSuccessfully() {
            // given
            String name = "10% 할인";
            Long quantity = 100L;
            Long discountValue = 10L;
            DiscountType discountType = DiscountType.PERCENT;
            CouponStatus couponStatus = CouponStatus.ACTIVE;
            LocalDateTime expiredAt = LocalDateTime.now().plusDays(10);

            // when
            Coupon coupon = Coupon.create(name, quantity, discountValue, discountType, couponStatus, expiredAt);

            // then
            assertThat(coupon.getName()).isEqualTo(name);
            assertThat(coupon.getQuantity()).isEqualTo(quantity);
            assertThat(coupon.getDiscountValue()).isEqualTo(discountValue);
            assertThat(coupon.getDiscountType()).isEqualTo(discountType);
            assertThat(coupon.getCouponStatus()).isEqualTo(couponStatus);
            assertThat(coupon.getExpiredAt()).isEqualTo(expiredAt);
        }

        @Test
        @DisplayName("쿠폰 이름이 null이면 예외를 던진다")
        void throwWhenNameIsNull() {
            assertThatThrownBy(() -> Coupon.create(null, 10L, 1000L,
                    DiscountType.FIXED, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰 이름은 필수입니다.");
        }

        @Test
        @DisplayName("쿠폰 수량이 0보다 작으면 예외를 던진다")
        void throwWhenQuantityIsNegative() {
            assertThatThrownBy(() -> Coupon.create("쿠폰", -1L, 1000L,
                    DiscountType.FIXED, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰 수량은 0보다 커야합니다.");
        }

        @Test
        @DisplayName("쿠폰 만료일이 현재보다 이전이면 예외를 던진다")
        void throwWhenExpiredAtIsPast() {
            assertThatThrownBy(() -> Coupon.create("쿠폰", 10L, 1000L,
                    DiscountType.FIXED, CouponStatus.ACTIVE, LocalDateTime.now().minusDays(1)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("쿠폰 만료일자는 현재 날짜보다 커야합니다.");
        }
    }

    @Nested
    @DisplayName("할인 금액 계산 테스트")
    class CalculateDiscount {

        @Test
        @DisplayName("정액 할인 쿠폰은 고정 금액을 반환한다")
        void fixedDiscount() {
            Coupon coupon = Coupon.create("1000원 할인", 10L, 1000L,
                    DiscountType.FIXED, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1));

            assertThat(coupon.calculateDiscount(5000L)).isEqualTo(1000L);
        }

        @Test
        @DisplayName("정률 할인 쿠폰은 퍼센트 계산을 반환한다")
        void percentDiscount() {
            Coupon coupon = Coupon.create("10% 할인", 10L, 10L,
                    DiscountType.PERCENT, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1));

            assertThat(coupon.calculateDiscount(10000L)).isEqualTo(1000L);
        }
    }

    @Nested
    @DisplayName("쿠폰 발급 테스트")
    class Issue {

        @Test
        @DisplayName("쿠폰 발급 시 수량이 감소한다")
        void issueCoupon() {
            Coupon coupon = Coupon.create("쿠폰", 5L, 1000L,
                    DiscountType.FIXED, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1));

            coupon.issue();

            assertThat(coupon.getQuantity()).isEqualTo(4L);
        }

        @Test
        @DisplayName("쿠폰 상태가 ACTIVE가 아니면 발급 불가")
        void throwWhenCouponStatusNotActive() {
            Coupon coupon = Coupon.create("쿠폰", 5L, 1000L,
                    DiscountType.FIXED, CouponStatus.EXPIRED, LocalDateTime.now().plusDays(1));

            assertThatThrownBy(coupon::issue)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("사용할 수 없는 쿠폰");
        }

        @Test
        @DisplayName("쿠폰 수량이 0이면 발급 불가")
        void throwWhenQuantityZero() {
            Coupon coupon = Coupon.create("쿠폰", 0L, 1000L,
                    DiscountType.FIXED, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(1));

            assertThatThrownBy(coupon::issue)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("쿠폰이 모두 소진되었습니다.");
        }
    }
}
