package com.loopers.interfaces.api.event.payment;

import com.loopers.application.payment.PaymentEventCriteria;
import com.loopers.application.payment.PaymentEventFacade;
import com.loopers.domain.payment.PaymentEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PaymentCompletedEventListenerTest {
    private final PaymentEventFacade paymentEventFacade = mock(PaymentEventFacade.class);
    private final PaymentCompletedEventListener listener = new PaymentCompletedEventListener(paymentEventFacade);

    @Nested
    @DisplayName("Payment 이벤트 리스너 테스트")
    class Event {

        @Test
        @DisplayName("결제 완료 이벤트를 수신하면 handlePaymentCompleted가 호출된다.")
        void callHandlePaymentCompleted_whenCompletedEventReceived() {
            // given
            Long userCouponId = 10L;
            String orderNumber = "ORD-123456";
            PaymentEvent.Completed event = PaymentEvent.Completed.of(userCouponId, orderNumber);

            // when
            listener.handle(event);

            // then
            ArgumentCaptor<PaymentEventCriteria.PaymentCompleted> captor =
                    ArgumentCaptor.forClass(PaymentEventCriteria.PaymentCompleted.class);

            verify(paymentEventFacade).handlePaymentCompleted(captor.capture());
            PaymentEventCriteria.PaymentCompleted captured = captor.getValue();

            assertThat(captured.getUserCouponId()).isEqualTo(userCouponId);
            assertThat(captured.getOrderNumber()).isEqualTo(orderNumber);
        }
    }
  
}
