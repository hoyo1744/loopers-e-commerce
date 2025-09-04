package com.loopers.application.eventlog;

import com.loopers.common.kafka.event.EventType;
import com.loopers.domain.eventhandled.EventHandledService;
import com.loopers.domain.eventlog.EventLog;
import com.loopers.domain.eventlog.EventLogCommand;
import com.loopers.domain.eventlog.EventLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraceEventConsumerHandlerTest {
    @Mock
    EventHandledService eventHandledService;

    @Mock
    EventLogService eventLogService;

    @InjectMocks
    TraceEventConsumerHandler handler;

    private TraceEventCriteria.Logged loggedEvent(String eventId) {
        return TraceEventCriteria.Logged.of(
                eventId,
                "trace-topic",
                1,
                456L,
                LocalDateTime.now(),
                "user-1",
                EventType.TRACE_ORDER_COMPLETED,
                "{\"orderId\":123}"
        );
    }

    @Test
    @DisplayName("최초 처리일 때만 createEventLog가 호출된다")
    void shouldCallCreateEventLogOnlyOnFirstHandling() {
        // given
        TraceEventCriteria.Logged event = loggedEvent("evt-trace-1");

        when(eventHandledService.processIfNotHandled(anyString(), anyString(), anyInt(), anyLong()))
                .thenReturn(true)
                .thenReturn(false)
                .thenReturn(false);

        when(eventLogService.createEventLog(any(EventLogCommand.Event.class)))
                .thenReturn(mock(EventLog.class));

        // when
        handler.handleTraceEvent(event);
        handler.handleTraceEvent(event);
        handler.handleTraceEvent(event);

        // then
        ArgumentCaptor<EventLogCommand.Event> cmdCaptor = ArgumentCaptor.forClass(EventLogCommand.Event.class);
        verify(eventLogService, times(1)).createEventLog(cmdCaptor.capture());

        EventLogCommand.Event cmd = cmdCaptor.getValue();
        assertThat(cmd.getUserId()).isEqualTo("user-1");
        assertThat(cmd.getEventType()).isEqualTo(event.getEventType());
        assertThat(cmd.getLog()).isEqualTo("{\"orderId\":123}");

        verify(eventHandledService, times(3))
                .processIfNotHandled(eq("evt-trace-1"), eq("trace-topic"), eq(1), eq(456L));

        verifyNoMoreInteractions(eventLogService);
    }

    @Test
    @DisplayName("이미 처리된 이벤트면 createEventLog는 호출되지 않는다")
    void shouldNotCallCreateEventLog_whenAlreadyHandled() {
        // given
        TraceEventCriteria.Logged event = loggedEvent("evt-trace-2");
        when(eventHandledService.processIfNotHandled(anyString(), anyString(), anyInt(), anyLong()))
                .thenReturn(false);

        // when
        handler.handleTraceEvent(event);

        // then
        verify(eventLogService, never()).createEventLog(any());
    }
}
