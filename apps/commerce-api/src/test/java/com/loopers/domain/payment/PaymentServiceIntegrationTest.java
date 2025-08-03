package com.loopers.domain.payment;

import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("결제 처리 통합 테스트")
    class Pay {

        @Test
        @DisplayName("유효한 주문 ID와 금액으로 결제 요청 시, 결제 상태가 PAID로 저장된다")
        void shouldSavePaymentWithStatusPaid_whenValidOrderIdAndAmountGiven() {
            // given
            Long orderId = 1L;
            Long amount = 10000L;

            PaymentCommand.Pay command = PaymentCommand.Pay.of(amount, orderId);

            // when
            PaymentInfo.Payment pay = paymentService.pay(command);

            // then
            Payment saved = paymentRepository.findById(pay.getPaymentId());
            assertThat(saved.getOrderId()).isEqualTo(orderId);
            assertThat(saved.getAmount()).isEqualTo(amount);
            assertThat(saved.getPaymentStatus()).isEqualTo(PaymentStatus.PAID);
        }

        @Test
        @DisplayName("결제 금액이 null 또는 0 이하인 경우 예외가 발생한다")
        void shouldThrow_whenInvalidAmountGiven() {
            // given
            PaymentCommand.Pay command = PaymentCommand.Pay.of(0L, 5000L);

            // when & then
            assertThatThrownBy(() -> paymentService.pay(command))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("유효하지 않은 금액입니다.");
        }

        @Test
        @DisplayName("주문 ID가 null 또는 0 이하인 경우 예외가 발생한다")
        void throwsException_whenOrderIdIsNullOrLessThanOrEqualToZero() {
            // given
            PaymentCommand.Pay command = PaymentCommand.Pay.of(1L, 0L);

            // when & then
            assertThatThrownBy(() -> paymentService.pay(command))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("유효하지 않은 주문 ID입니다.");
        }
    }
}
