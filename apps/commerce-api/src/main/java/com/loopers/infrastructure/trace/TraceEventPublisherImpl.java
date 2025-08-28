package com.loopers.infrastructure.trace;

import com.loopers.domain.trace.TraceEvent;
import com.loopers.domain.trace.TraceEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraceEventPublisherImpl implements TraceEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void publish(TraceEvent event) {
        eventPublisher.publishEvent(event);
    }
}
