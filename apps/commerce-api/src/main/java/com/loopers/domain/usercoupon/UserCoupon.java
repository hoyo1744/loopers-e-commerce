package com.loopers.domain.usercoupon;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "user_coupon")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon {

    @Id
    @Column(name = "user_coupon_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Long couponId;

    private LocalDateTime usedAt;

    private LocalDateTime issuedAt;

    @Enumerated(EnumType.STRING)
    private UserCouponStatus userCouponStatus;

    @Builder
    private UserCoupon(Long id, String userId, Long couponId, LocalDateTime usedAt, LocalDateTime issuedAt, UserCouponStatus userCouponStatus) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.usedAt = usedAt;
        this.issuedAt = issuedAt;
        this.userCouponStatus = userCouponStatus;
    }

    public static UserCoupon create(String userId, Long couponId) {

        return UserCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .issuedAt(LocalDateTime.now())
                .userCouponStatus(UserCouponStatus.NO_USED)
                .build();
    }

    public Boolean isUsable() {
        return this.userCouponStatus.isUsable();
    }


    public void use() {
        this.userCouponStatus.validateUsable();
        this.userCouponStatus = UserCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

}
