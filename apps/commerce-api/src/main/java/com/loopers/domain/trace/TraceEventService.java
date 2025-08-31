package com.loopers.domain.trace;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TraceEventService {

    private final TraceEventPublisher traceEventPublisher;

    public void publish(TraceEvent event) {
        traceEventPublisher.publish(event);
    }
}
