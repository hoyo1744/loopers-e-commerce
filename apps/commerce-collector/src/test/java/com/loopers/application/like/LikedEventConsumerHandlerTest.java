package com.loopers.application.like;

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
class LikedEventConsumerHandlerTest {

    @Mock
    EventHandledService eventHandledService;

    @Mock
    ProductMetricService productMetricService;

    @InjectMocks
    LikeEventConsumerHandler handler;

    private LikedEventCriteria.Change likeEvent(String eventId) {
        return LikedEventCriteria.Change.of(
                eventId,
                "like-topic",
                0,
                123L,
                101L,
                LocalDateTime.now(),
                true
        );


    }

    @DisplayName("중복 이벤트가 순차적으로 도착해도 upsertLike는 한 번만 호출된다.")
    @Test
    void upsertLikeIsCalledOnlyOnce_whenDuplicateEventsArriveSequentially() {

        // given
        LikedEventCriteria.Change event = likeEvent("evt-1");

        when(eventHandledService.processIfNotHandled(anyString(), anyString(), anyInt(), anyLong()))
                .thenReturn(true)
                .thenReturn(false)
                .thenReturn(false);

        // when
        handler.handleLikeEvent(event);
        handler.handleLikeEvent(event);
        handler.handleLikeEvent(event);

        // then
        verify(productMetricService, times(1))
                .upsertLike(eq(101L), any(LocalDate.class), eq(1L), any(LocalDateTime.class));

        verify(eventHandledService, times(3))
                .processIfNotHandled(eq("evt-1"), eq("like-topic"), eq(0), eq(123L));

        verifyNoMoreInteractions(productMetricService);
    }

    @DisplayName("이벤트가 이미 처리된 경우 upsertLike는 호출되지 않는다.")
    @Test
    void upsertLikeIsNotCalled_whenEventHandledServiceReturnsFalse() {
        // given
        LikedEventCriteria.Change event = likeEvent("evt-3");


        // when
        when(eventHandledService.processIfNotHandled(anyString(), anyString(), anyInt(), anyLong()))
                .thenReturn(false);

        handler.handleLikeEvent(event);

        // then
        verify(productMetricService, never())
                .upsertLike(anyLong(), any(LocalDate.class), anyLong(), any(LocalDateTime.class));
    }
}
