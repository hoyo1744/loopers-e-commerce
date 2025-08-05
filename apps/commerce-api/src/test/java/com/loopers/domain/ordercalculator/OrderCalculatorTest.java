package com.loopers.domain.ordercalculator;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.order.Order;
import com.loopers.domain.usercoupon.UserCoupon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class OrderCalculatorTest {

    private final OrderCalculator orderCalculator = new OrderCalculator();

    @Test
    @DisplayName("주문 총액을 기반으로 쿠폰 할인 적용을 수행한다")
    void applyDiscount_shouldApplyCalculatedDiscount() {
        // given
        Order order = mock(Order.class);
        Coupon coupon = mock(Coupon.class);
        UserCoupon userCoupon = mock(UserCoupon.class);

        Long totalPrice = 10_000L;
        Long discountAmount = 2_000L;
        Long userCouponId = 1L;

        when(order.getTotalPrice()).thenReturn(totalPrice);
        when(coupon.calculateDiscount(totalPrice)).thenReturn(discountAmount);
        when(userCoupon.getId()).thenReturn(userCouponId);

        // when
        orderCalculator.applyDiscount(order, coupon, userCoupon);

        // then
        verify(order).getTotalPrice();
        verify(coupon).calculateDiscount(totalPrice);
        verify(userCoupon).getId();
        verify(order).applyDiscount(userCouponId, discountAmount);
        verifyNoMoreInteractions(order, coupon, userCoupon);
    }
}
