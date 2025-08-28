package com.loopers.domain.trace;

public interface TraceEventPublisher {
    void publish(TraceEvent event);
}
