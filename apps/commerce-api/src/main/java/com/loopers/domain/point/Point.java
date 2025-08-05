package com.loopers.domain.point;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

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
            throw new IllegalArgumentException("포인트 충전 금액은 0 보다 커야 합니다.");
        }

        this.amount += amount;
    }

    public void deduct(Long amount) {
        validationAmount(amount);
        if (this.amount < amount) {
            throw new CoreException(ErrorType.CONFLICT, "포인트가 부족합니다. 현재 포인트: " + this.amount + ", 요청 포인트: " + amount);
        }
        this.amount -= amount;
    }

    public static Point create(String userId, Long amount) {
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
