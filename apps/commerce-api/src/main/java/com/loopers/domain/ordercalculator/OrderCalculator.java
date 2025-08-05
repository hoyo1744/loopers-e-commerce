package com.loopers.domain.ordercalculator;

import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.order.Order;
import com.loopers.domain.usercoupon.UserCoupon;
import org.springframework.stereotype.Service;

@Service
public class OrderCalculator {

    public void applyDiscount(Order order, Coupon coupon, UserCoupon userCoupon) {
        Long totalPrice = order.getTotalPrice();
        Long discountPrice = coupon.calculateDiscount(totalPrice);
        order.applyDiscount(userCoupon.getId(), discountPrice);
    }
}
