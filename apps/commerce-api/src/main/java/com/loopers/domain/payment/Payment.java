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

    private String orderNumber;

    private Long amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    private CardType cardType;

    @Builder
    private Payment(Long orderId, Long amount, String orderNumber, PaymentStatus paymentStatus, PaymentType paymentType, CardType cardType) {
        this.orderId = orderId;
        this.amount = amount;
        this.orderNumber = orderNumber;
        this.paymentStatus = paymentStatus;
        this.paymentType = paymentType;
        this.cardType = cardType;
    }

    public static Payment cardPaymentCreate(Long orderId, Long amount, String orderNumber, CardType cardType) {
        return create(orderId, amount, orderNumber, PaymentType.CARD, cardType);
    }

    public static Payment pointPaymentCreate(Long orderId, Long amount, String orderNumber) {
        return create(orderId, amount, orderNumber, PaymentType.POINT, null);
    }

    public static Payment create(Long orderId, Long amount, String orderNumber, PaymentType paymentType, CardType cardType) {
        validationOrderId(orderId);
        validationAmount(amount);
        validationOrderNumber(orderNumber);
        validationPaymentType(paymentType);

        if(paymentType == PaymentType.CARD && cardType == null) {
            throw new IllegalArgumentException("결제타입이 카드인 경우 ,카드 타입은 필수입니다.");
        }

        if(paymentType == PaymentType.POINT && cardType != null) {
            throw new IllegalArgumentException("포인트 결제 시, 카드 타입을 지정할 수 없습니다.");
        }

        return Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .orderNumber(orderNumber)
                .paymentStatus(PaymentStatus.READY)
                .paymentType(paymentType)
                .cardType(cardType)
                .build();
    }

    public static void validationPaymentType(PaymentType paymentType) {
        if(paymentType == null) {
            throw new IllegalArgumentException("결제 타입은 필수압니다.");
        }
    }

    public static void validationOrderNumber(String orderNumber) {
        if(orderNumber == null || orderNumber.isBlank()) {
            throw new IllegalArgumentException("주문번호는 필수입니다.");
        }

        if (orderNumber.length() < 6) {
            throw new IllegalArgumentException("주문 번호는 6자리보다 커야합니다.");
        }
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

    public void pending() {
        this.paymentStatus = PaymentStatus.PENDING;
    }


}
