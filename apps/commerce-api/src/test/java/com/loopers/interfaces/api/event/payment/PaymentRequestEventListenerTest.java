package com.loopers.interfaces.api.event.payment;

import com.loopers.application.payment.PaymentEventCriteria;
import com.loopers.application.payment.PaymentEventFacade;
import com.loopers.domain.payment.CardType;
import com.loopers.domain.payment.PaymentEvent;
import com.loopers.domain.payment.PaymentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaymentRequestEventListenerTest {

    private final PaymentEventFacade paymentEventFacade = mock(PaymentEventFacade.class);
    private final PaymentRequestEventListener listener = new PaymentRequestEventListener(paymentEventFacade);

    @Nested
    @DisplayName("결제 요청 이벤트 리스너 테스트")
    class Event {

        @Test
        @DisplayName("결제 요청 이벤트를 수신하면 handlePaymentRequest 가 호출된다.")
        void callHandlePaymentRequest_whenRequestEventReceived() {
            // given
            String userId = "user123";
            Long couponId = 100L;
            String orderNumber = UUID.randomUUID().toString();
            Long amount = 10_000L;
            CardType cardType = CardType.SAMSUNG;
            String cardNo = "1234-5678-9012-3456";
            PaymentType paymentType = PaymentType.CARD;

            PaymentEvent.Request event = PaymentEvent.Request.of(userId, couponId,
                    PaymentEvent.Payment.of(orderNumber, cardType, cardNo, amount, paymentType)
            );

            // when
            listener.handle(event);

            // then
            ArgumentCaptor<PaymentEventCriteria.PaymentRequest> captor =
                    ArgumentCaptor.forClass(PaymentEventCriteria.PaymentRequest.class);

            verify(paymentEventFacade).handlePaymentRequest(captor.capture());
            PaymentEventCriteria.PaymentRequest captured = captor.getValue();

            assertThat(captured.getUserId()).isEqualTo(userId);
            assertThat(captured.getCouponId()).isEqualTo(couponId);
            assertThat(captured.getPayment().getOrderNumber()).isEqualTo(orderNumber);
            assertThat(captured.getPayment().getAmount()).isEqualTo(amount);
            assertThat(captured.getPayment().getCardType()).isEqualTo(cardType);
            assertThat(captured.getPayment().getCardNo()).isEqualTo(cardNo);
            assertThat(captured.getPayment().getPaymentType()).isEqualTo(paymentType);
        }
    }
}
