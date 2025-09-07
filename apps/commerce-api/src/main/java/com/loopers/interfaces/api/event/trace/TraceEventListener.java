package com.loopers.interfaces.api.event.trace;

import com.loopers.common.kafka.event.EventMessage;
import com.loopers.common.kafka.event.EventType;
import com.loopers.common.kafka.event.trace.TraceOutEvent;
import com.loopers.common.kafka.topic.Topics;
import com.loopers.domain.sender.MessageSender;

import com.loopers.domain.trace.TraceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
@Slf4j
@RequiredArgsConstructor
public class TraceEventListener {

    private final MessageSender messageSender;

    private final TraceEventMessageFactory traceEventMessageFactory;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void logTrace(TraceEvent event) {
        messageSender.send(Topics.TRACE, event.getUserId().toString(),
                traceEventMessageFactory.create(event)
                );
        log.info("[TRACE] event={}, userId={}, payload={}", event.getEvent(), event.getUserId(), event.getPayload());
    }
}
