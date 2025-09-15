package com.loopers.application.order;

import com.loopers.common.kafka.topic.Topics;
import com.loopers.domain.eventhandled.EventHandledService;
import com.loopers.domain.metric.ProductMetricService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerHandlerTest {

    @Mock
    EventHandledService eventHandledService;

    @Mock
    ProductMetricService productMetricService;

    @InjectMocks
    OrderEventConsumerHandler handler;

    private OrderEventCriteria.Order orderEvent(String eventId, Long productId, Long orderId, String orderNumber, Long quantity) {
        return OrderEventCriteria.Order.of(
                eventId,
                Topics.ORDER,
                0,
                1L,
                productId,
                orderId,
                orderNumber,
                quantity,
                LocalDateTime.now()
        );
    }

    @DisplayName("중복 주문 이벤트가 순차적으로 도착해도 upsertSales는 한 번만 호출된다.")
    @Test
    void upsertSalesIsCalledOnlyOnce_whenDuplicateOrderEventsArriveSequentially() {
        // given
        Long productId = 1L;
        Long orderId = 1L;
        Long quantity = 1L;
        String orderNumber = UUID.randomUUID().toString();
        String eventId = UUID.randomUUID().toString();
        OrderEventCriteria.Order event = orderEvent(eventId,  productId, orderId, orderNumber, quantity);

        when(eventHandledService.processIfNotHandled(anyString(), anyString(), anyInt(), anyLong()))
                .thenReturn(true)
                .thenReturn(false)
                .thenReturn(false);

        // when
        handler.handleOrderEvent(event);
        handler.handleOrderEvent(event);
        handler.handleOrderEvent(event);


        // then
        verify(productMetricService, times(1))
                .upsertSales(eq(orderId), eq(event.metricDate()), eq(1L), eq(event.getEventTime()));

        verifyNoMoreInteractions(productMetricService);
    }

    @DisplayName("이벤트가 이미 처리된 경우 upsertSales는 호출되지 않는다.")
    @Test
    void upsertSalesIsNotCalled_whenEventHandledServiceReturnsFalse() {
        // given
        OrderEventCriteria.Order event = orderEvent(UUID.randomUUID().toString(), 1L, 1L, UUID.randomUUID().toString(), 1L);

        when(eventHandledService.processIfNotHandled(anyString(), anyString(), anyInt(), anyLong()))
                .thenReturn(false);

        // when
        handler.handleOrderEvent(event);

        // then
        verify(productMetricService, never())
                .upsertSales(anyLong(), any(LocalDate.class), anyLong(), any(LocalDateTime.class));
    }
}
