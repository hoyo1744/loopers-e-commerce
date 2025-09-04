package com.loopers.application.stock;

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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockEventConsumerHandlerTest {

    @Mock
    EventHandledService eventHandledService;

    @Mock
    ProductMetricService productMetricService;

    @InjectMocks
    StockEventConsumerHandler handler;

    private StockEventCriteria.Adjusted stockEvent(String eventId) {
        return StockEventCriteria.Adjusted.of(
                eventId,
                "stock-topic",
                2,
                987L,
                LocalDateTime.now(),
                202L,
                3L,
                true
        );
    }

    @Test
    @DisplayName("중복 이벤트가 수신되더라도 upsertSales는 한 번만 호출된다")
    void shouldCallUpsertSalesOnlyOnce_whenDuplicateEventsArriveSequentially() {
        // given
        StockEventCriteria.Adjusted event = stockEvent("evt-stock-1");

        when(eventHandledService.processIfNotHandled(anyString(), anyString(), anyInt(), anyLong()))
                .thenReturn(true)
                .thenReturn(false)
                .thenReturn(false);

        // when
        handler.handleStockEvent(event);
        handler.handleStockEvent(event);
        handler.handleStockEvent(event);

        // then
        verify(productMetricService, times(1))
                .upsertSales(eq(202L), any(LocalDate.class), eq(-3L), any(LocalDateTime.class));

        verify(eventHandledService, times(3))
                .processIfNotHandled(eq("evt-stock-1"), eq("stock-topic"), eq(2), eq(987L));

        verifyNoMoreInteractions(productMetricService);
    }

    @Test
    @DisplayName("이미 처리된 이벤트면 upsertSales는 호출되지 않는다")
    void shouldNotCallUpsertSales_whenAlreadyHandled() {
        // given
        var event = stockEvent("evt-stock-2");
        when(eventHandledService.processIfNotHandled(anyString(), anyString(), anyInt(), anyLong()))
                .thenReturn(false);

        // when
        handler.handleStockEvent(event);

        // then
        verify(productMetricService, never())
                .upsertSales(anyLong(), any(LocalDate.class), anyLong(), any(LocalDateTime.class));
    }
}
