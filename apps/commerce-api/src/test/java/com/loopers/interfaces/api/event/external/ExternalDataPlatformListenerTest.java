package com.loopers.interfaces.api.event.external;

import com.loopers.domain.external.ExternalDataService;
import com.loopers.domain.external.OrderResultPayload;
import com.loopers.domain.order.OrderEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ExternalDataPlatformListenerTest {

    private final ExternalDataService externalDataService = mock(ExternalDataService.class);
    private final ExternalDataPlatformListener listener = new ExternalDataPlatformListener(externalDataService);

    @Nested
    @DisplayName("주문 완료 외부 데이터 연동 이벤트 테스트")
    class Event {

        @Test
        @DisplayName("Order 완료 이벤트를 수신하면 외부 전송 함수가 호출된다.")
        void callSendToExternalPlatform_whenOrderCompletedEventReceived() {
            // given
            Long orderId = 1L;
            String orderNumber = "ORDER-123456";
            Long amount = 15000L;
            OrderEvent.Completed event = OrderEvent.Completed.of(orderId, orderNumber, amount);

            // when
            listener.handle(event);

            // then
            ArgumentCaptor<String> typeCaptor = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<OrderResultPayload> payloadCaptor = ArgumentCaptor.forClass(OrderResultPayload.class);

            verify(externalDataService).send(typeCaptor.capture(), payloadCaptor.capture());

            assertThat(typeCaptor.getValue()).isEqualTo("ORDER");

            OrderResultPayload payload = payloadCaptor.getValue();
            assertThat(payload.getOrderId()).isEqualTo(orderId);
            assertThat(payload.getOrderNumber()).isEqualTo(orderNumber);
            assertThat(payload.getAmount()).isEqualTo(amount);
        }
    }
}
