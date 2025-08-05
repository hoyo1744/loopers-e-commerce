package com.loopers.domain.coupon;

import com.loopers.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Column(name = "coupon_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long quantity;

    private Long discountValue;

    @Enumerated(EnumType.STRING)
    DiscountType discountType;

    @Enumerated(EnumType.STRING)
    CouponStatus couponStatus;

    LocalDateTime expiredAt;

    @Builder
    private Coupon(String name, Long quantity, Long discountValue, DiscountType discountType, CouponStatus couponStatus, LocalDateTime expiredAt) {
        this.name = name;
        this.quantity = quantity;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.couponStatus = couponStatus;
        this.expiredAt = expiredAt;
    }

    public long discountAmount(Long totalPrice) {
        if (discountType == DiscountType.FIXED) {
            return Math.min(discountValue, totalPrice);
        } else if (discountType == DiscountType.PERCENT) {
            double percent = discountValue.doubleValue(); // 12.5 가능
            double discount = totalPrice * percent / 100.0;
            return (long) discount; // 절삭
        } else {
            throw new IllegalStateException("지원하지 않는 할인 타입입니다.");
        }
    }

    public static Coupon create(String name, Long quantity, Long discountValue, DiscountType discountType, CouponStatus couponStatus, LocalDateTime expiredAt) {

        validationName(name);
        validationQuantity(quantity);
        validationDiscountValue(discountValue);
        validationDiscountType(discountType);
        validationCouponStatus(couponStatus);
        validationExpiredAt(expiredAt);

        return Coupon.builder()
                .name(name)
                .quantity(quantity)
                .discountValue(discountValue)
                .discountType(discountType)
                .couponStatus(couponStatus)
                .expiredAt(expiredAt)
                .build();
    }

    public static void validationName(String name) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("쿠폰 이름은 필수입니다.");
        }
    }

    public static void validationQuantity(Long quantity) {
        if(quantity == null || quantity < 0) {
            throw new IllegalArgumentException("쿠폰 수량은 0보다 커야합니다.");
        }
    }

    public static void validationDiscountValue(Long discountValue) {
        if(discountValue == null || discountValue < 0) {
            throw new IllegalArgumentException("할인율은 0보다 작을 수 없습니다.");
        }
    }

    public static void validationDiscountType(DiscountType discountType) {
        if(discountType == null ) {
            throw new IllegalArgumentException("쿠폰 타입은 필수입니다.");
        }
    }

    public static void validationCouponStatus(CouponStatus couponStatus) {
        if(couponStatus == null ) {
            throw new IllegalArgumentException("쿠폰 상태는 필수입니다.");
        }
    }

    public static void validationExpiredAt(LocalDateTime expiredAt) {
        if(expiredAt == null  || expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("쿠폰 만료일자는 현재 날짜보다 커야합니다.");
        }
    }

    public void issue() {
        this.couponStatus.validateUsable();

        if (this.expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("만료된 쿠폰은 발급할 수 없습니다.");
        }

        if (this.quantity <= 0) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }

        this.quantity -= 1;

    }

    public Long calculateDiscount(Long totalPrice) {
        if (discountType == DiscountType.FIXED) {
            return Math.min(discountValue, totalPrice);
        }

        if (discountType == DiscountType.PERCENT) {
            return (totalPrice * discountValue) / 100;
        }

        throw new IllegalStateException("지원하지 않는 할인 타입입니다.");
    }


}
