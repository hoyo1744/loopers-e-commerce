package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentTest {

    @Nested
    @DisplayName("Payment 생성 테스트")
    class Create {

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {0L, -1L})
        @DisplayName("주문 ID가 null 또는 0 이하일 경우 예외를 발생시킨다")
        void throwException_whenOrderIdInvalid(Long orderId) {
            // given
            Long amount = 1000L;

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                Payment.pointPaymentCreate(orderId, amount, UUID.randomUUID().toString());
            });

            assertThat(exception)
                    .isInstanceOf(CoreException.class)
                    .hasMessage("유효하지 않은 주문 ID입니다.");
        }

        @ParameterizedTest
        @NullSource
        @ValueSource(longs = {0L, -100L})
        @DisplayName("금액이 null 또는 0 이하일 경우 예외를 발생시킨다")
        void throwException_whenAmountInvalid(Long amount) {
            // given
            Long orderId = 1L;

            // when & then
            CoreException exception = assertThrows(CoreException.class, () -> {
                Payment.pointPaymentCreate(orderId, amount, UUID.randomUUID().toString());
            });

            assertThat(exception)
                    .isInstanceOf(CoreException.class)
                    .hasMessage("유효하지 않은 금액입니다.");
        }

        @Test
        @DisplayName("올바른 주문 ID와 금액으로 생성 시 PENDING 상태의 Payment를 반환한다")
        void createPayment_whenValidInput() {
            // given
            Long orderId = 1L;
            Long amount = 5000L;

            // when
            Payment payment = Payment.pointPaymentCreate(orderId, amount, UUID.randomUUID().toString());

            // then
            assertThat(payment.getOrderId()).isEqualTo(orderId);
            assertThat(payment.getAmount()).isEqualTo(amount);
            assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.READY);
        }
    }

    @Nested
    @DisplayName("결제 상태 변경 테스트")
    class Status {

        @Test
        @DisplayName("pay() 호출 시 결제 상태가 PAID로 변경된다")
        void shouldChangeStatusToPaid() {
            // given
            Payment payment = Payment.pointPaymentCreate(1L, 3000L, UUID.randomUUID().toString());

            // when
            payment.pay();

            // then
            assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        }
    }
}
