package com.loopers.application.pageview;

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
class PageViewEventConsumerHandlerTest {

    @Mock
    EventHandledService eventHandledService;

    @Mock
    ProductMetricService productMetricService;

    @InjectMocks
    PageViewEventConsumerHandler handler;

    private PageViewEventCriteria.View view(String eventId, Long productId) {
        return PageViewEventCriteria.View.of(
                eventId,
                Topics.PAGE_VIEW,
                0,
                1L,
                productId,
                LocalDateTime.now()
        );
    }
    @Test
    @DisplayName("중복 페이지뷰 이벤트가 순차적으로 도착해도 upsertPageView는 한 번만 호출된다.")
    void upsertPageViewIsCalledOnlyOnce_whenDuplicateEventsArriveSequentially() {
        // given
        String eventId = UUID.randomUUID().toString();
        Long productId = 1L;

        PageViewEventCriteria.View event =
                view(eventId, productId);

        when(eventHandledService.processIfNotHandled(eventId, event.getTopic(), event.getPartition(), event.getOffset()))
                .thenReturn(true)
                .thenReturn(false)
                .thenReturn(false);

        // when
        handler.handlePageViewEvent(event);
        handler.handlePageViewEvent(event);
        handler.handlePageViewEvent(event);

        // then
        verify(productMetricService, times(1))
                .upsertPageView(eq(productId), eq(event.metricDate()), eq(1L), eq(event.getEventTime()));

        verify(eventHandledService, times(3))
                .processIfNotHandled(eq(eventId), eq(event.getTopic()), eq(event.getPartition()), eq(event.getOffset()));

        verifyNoMoreInteractions(productMetricService);
    }

    @Test
    @DisplayName("이벤트가 이미 처리된 경우 upsertPageView는 호출되지 않는다.")
    void upsertPageViewIsNotCalled_whenEventAlreadyHandled() {
        // given
        String eventId = UUID.randomUUID().toString();
        Long productId = 1L;

        PageViewEventCriteria.View event =
                view(eventId, productId);


        when(eventHandledService.processIfNotHandled(anyString(), anyString(), anyInt(), anyLong()))
                .thenReturn(false);

        // when
        handler.handlePageViewEvent(event);

        // then
        verify(productMetricService, never())
                .upsertPageView(anyLong(), any(LocalDate.class), anyLong(), any(LocalDateTime.class));
    }
}
