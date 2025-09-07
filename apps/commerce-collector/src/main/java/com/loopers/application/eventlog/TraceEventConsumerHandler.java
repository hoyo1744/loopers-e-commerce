package com.loopers.application.eventlog;

import com.loopers.common.kafka.event.EventType;
import com.loopers.domain.eventhandled.EventHandledService;
import com.loopers.domain.eventlog.EventLogCommand;
import com.loopers.domain.eventlog.EventLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class TraceEventConsumerHandler {

    private final EventHandledService eventHandledService;

    private final EventLogService eventLogService;

    @Transactional
    public void handleTraceEvent(TraceEventCriteria.Log event) {

        boolean isNewEvent = eventHandledService.processIfNotHandled(event.getEventId(), event.getTopic(), event.getPartition(),
                event.getOffset());
        if (!isNewEvent) {
            return;
        }

        eventLogService.createEventLog(EventLogCommand.Event.of(event.getUserId(), event.getEventType(), event.getLog()));
    }


}
