package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @Column(name = "payment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private Long amount;

    private PaymentStatus paymentStatus;

    @Builder
    private Payment(Long orderId, Long amount, PaymentStatus paymentStatus) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
    }

    public static Payment create(Long orderId, Long amount) {
        validationOrderId(orderId);
        validationAmount(amount);

        return Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .paymentStatus(PaymentStatus.READY)
                .build();
    }

    private static void validationAmount(Long amount) {
        if (amount == null || amount <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 금액입니다.");
        }
    }

    private static void validationOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 주문 ID입니다.");
        }
    }

    public void pay() {
        this.paymentStatus = PaymentStatus.PAID;
    }

}
