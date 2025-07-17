package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "point")
public class Point {
    @Id
    @Column(name = "point_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    private Long amount;

    @Builder
    private Point(String userId, Long amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public void charge(Long amount) {
        if (amount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트 충전 금액은 0 보다 커야 합니다.");
        }
    }

    public static Point of(String userId, Long amount) {
        validationAmount(amount);

        return Point.builder()
                .userId(userId)
                .amount(amount)
                .build();
    }

    public static void validationAmount(Long amount) {
        if (amount < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트 금액은 0 보다 작을 수 없습니다.");
        }
    }


}
